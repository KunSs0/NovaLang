package com.novalang.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Workspace 资源所有权树中的一个可销毁节点。
 *
 * <p>子作用域和资源均按注册逆序释放，保证后创建的阶段、监听器及任务先退出。销毁
 * 某个资源失败不会阻断剩余清理，最终异常会通过 suppressed exceptions 完整报告。</p>
 */
public final class ResourceScope implements AutoCloseable {

    private final ScopeType type;
    private final String ownerId;
    private final ResourceScope parent;
    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private final ReentrantLock executionLock = new ReentrantLock(true);
    private final List<ResourceScope> children = new ArrayList<ResourceScope>();
    private final List<WorkspaceResource> resources = new ArrayList<WorkspaceResource>();
    private volatile ResourceScopeState state = ResourceScopeState.ACTIVE;

    /**
     * 创建 Generation 根作用域。
     *
     * @param ownerId Workspace 或 Generation 标识
     * @return 新的根作用域
     * @throws IllegalArgumentException ownerId 为空时抛出
     */
    public static ResourceScope generation(String ownerId) {
        return new ResourceScope(ScopeType.GENERATION, ownerId, null);
    }

    /**
     * 创建一个资源作用域节点并记录其父级关系。
     *
     * @param type 作用域类型
     * @param ownerId 宿主所有者标识
     * @param parent 父作用域；Generation 根为 {@code null}
     */
    private ResourceScope(ScopeType type, String ownerId, ResourceScope parent) {
        if (type == null) {
            throw new IllegalArgumentException("ScopeType must not be null");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("ownerId must not be blank");
        }
        this.type = type;
        this.ownerId = ownerId;
        this.parent = parent;
    }

    /** @return 作用域类型 */
    public ScopeType getType() {
        return type;
    }

    /** @return 宿主所有者标识 */
    public String getOwnerId() {
        return ownerId;
    }

    /** @return 父作用域；Generation 根返回 {@code null} */
    public ResourceScope getParent() {
        return parent;
    }

    /** @return 当前生命周期状态 */
    public ResourceScopeState getState() {
        return state;
    }

    /**
     * 在当前节点下创建子作用域。
     *
     * @param childType 子作用域类型
     * @param childOwnerId 子作用域宿主标识
     * @return 已挂入资源树的子作用域
     * @throws WorkspaceException 当前作用域不活跃或请求嵌套 Generation 时抛出
     */
    public ResourceScope openChild(ScopeType childType, String childOwnerId) {
        if (childType == ScopeType.GENERATION) {
            throw new WorkspaceException("A Generation scope can only be the resource tree root");
        }
        lifecycleLock.lock();
        try {
            requireActive("open a child scope");
            ResourceScope child = new ResourceScope(childType, childOwnerId, this);
            children.add(child);
            return child;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 将宿主资源登记到当前作用域。
     *
     * @param resource 已成功创建的宿主资源
     * @param <T> 具体资源类型
     * @return 原资源，方便宿主 API 直接返回句柄
     * @throws WorkspaceException 当前作用域不活跃时抛出
     */
    public <T extends WorkspaceResource> T register(T resource) {
        if (resource == null) {
            throw new IllegalArgumentException("resource must not be null");
        }
        lifecycleLock.lock();
        try {
            requireActive("register a resource");
            resources.add(resource);
            return resource;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 移除已经自行结束或取消的资源登记，不再次调用资源的销毁方法。
     *
     * <p>该方法供通用任务和订阅句柄在正常结束后释放作用域引用。Workspace dispose
     * 与资源主动结束发生竞态时，任一方先移除都不会造成重复销毁。</p>
     *
     * @param resource 已经结束的资源
     * @return 当前作用域确实持有并移除该资源时返回 {@code true}
     */
    public boolean unregister(WorkspaceResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("resource must not be null");
        }
        lifecycleLock.lock();
        try {
            return resources.remove(resource);
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 返回当前直接子作用域快照。
     *
     * @return 不可变子作用域列表
     */
    public List<ResourceScope> getChildren() {
        lifecycleLock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<ResourceScope>(children));
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 返回当前直接登记资源数量。
     *
     * @return 资源数量
     */
    public int getResourceCount() {
        lifecycleLock.lock();
        try {
            return resources.size();
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 判断当前作用域是否属于指定资源树。
     *
     * @param expectedRoot 预期 Generation 根作用域
     * @return 当前作用域自身或任一祖先等于指定根时返回 {@code true}
     */
    public boolean belongsTo(ResourceScope expectedRoot) {
        if (expectedRoot == null) {
            return false;
        }
        ResourceScope current = this;
        while (current != null) {
            if (current == expectedRoot) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }

    /**
     * 在该作用域的公平互斥锁内执行任务。
     *
     * @param action 待执行逻辑
     * @param <T> 返回值类型
     * @return 执行结果
     * @throws Exception 原始执行异常
     */
    <T> T executeSerial(Callable<T> action) throws Exception {
        executionLock.lock();
        try {
            requireActive("execute serial script code");
            return action.call();
        } finally {
            executionLock.unlock();
        }
    }

    /**
     * 递归释放子作用域和资源。
     *
     * <p>方法幂等；第一次调用之后再次调用不会重复释放资源。</p>
     *
     * @throws WorkspaceException 一个或多个资源释放失败时抛出
     */
    public void dispose() {
        List<ResourceScope> childSnapshot;
        List<WorkspaceResource> resourceSnapshot;

        lifecycleLock.lock();
        try {
            if (state == ResourceScopeState.DISPOSED || state == ResourceScopeState.DISPOSING) {
                return;
            }
            state = ResourceScopeState.DISPOSING;
            childSnapshot = new ArrayList<ResourceScope>(children);
            resourceSnapshot = new ArrayList<WorkspaceResource>(resources);
            children.clear();
            resources.clear();
        } finally {
            lifecycleLock.unlock();
        }

        WorkspaceException failure = null;
        // 子作用域先于本节点资源，并且二者都按创建逆序释放。
        for (int index = childSnapshot.size() - 1; index >= 0; index--) {
            try {
                childSnapshot.get(index).dispose();
            } catch (RuntimeException exception) {
                failure = appendFailure(failure, exception);
            }
        }
        for (int index = resourceSnapshot.size() - 1; index >= 0; index--) {
            try {
                resourceSnapshot.get(index).dispose();
            } catch (Exception exception) {
                failure = appendFailure(failure, exception);
            }
        }

        lifecycleLock.lock();
        try {
            state = ResourceScopeState.DISPOSED;
        } finally {
            lifecycleLock.unlock();
        }
        detachFromParent();

        if (failure != null) {
            throw failure;
        }
    }

    /**
     * {@link AutoCloseable} 入口，等价于 {@link #dispose()}。
     */
    @Override
    public void close() {
        dispose();
    }

    /**
     * 校验当前作用域仍可接受指定操作。
     *
     * @param operation 英文操作描述
     * @throws WorkspaceException 作用域不活跃时抛出
     */
    private void requireActive(String operation) {
        if (state != ResourceScopeState.ACTIVE) {
            throw new WorkspaceException("Scope is not active and cannot " + operation + ": "
                    + ownerId + " (" + state + ")");
        }
    }

    /**
     * 从父作用域的直接子节点集合中移除当前节点。
     */
    private void detachFromParent() {
        if (parent == null) {
            return;
        }
        parent.lifecycleLock.lock();
        try {
            parent.children.remove(this);
        } finally {
            parent.lifecycleLock.unlock();
        }
    }

    /**
     * 聚合资源销毁异常，同时保留首个失败作为主异常。
     *
     * @param aggregate 已存在的聚合异常
     * @param failure 本次资源销毁异常
     * @return 新建或追加后的聚合异常
     */
    private WorkspaceException appendFailure(WorkspaceException aggregate, Throwable failure) {
        if (aggregate == null) {
            return new WorkspaceException("Failed to dispose scope resources: " + ownerId, failure);
        }
        aggregate.addSuppressed(failure);
        return aggregate;
    }
}
