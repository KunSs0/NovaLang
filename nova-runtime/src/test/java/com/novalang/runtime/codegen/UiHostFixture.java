package com.novalang.runtime.codegen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模拟 hotbar 迁移涉及的 Zeus 宿主边界。
 */
public final class UiHostFixture {

    private final LifecycleLog lifecycleLog = new LifecycleLog();
    private final LibRegistry libs = new LibRegistry();
    private final NodeFixture root = new NodeFixture("root");
    private final BoxNodeFixture slots = new BoxNodeFixture("slots");
    private final BoxNodeFixture tip = new BoxNodeFixture("tip");
    private final Graphics graphics = new Graphics(lifecycleLog);
    private final EventBus events = new EventBus(lifecycleLog);
    private final TweenTool tween = new TweenTool(lifecycleLog);

    public UiHostFixture() {
        root.addChild("slots", slots);
        root.addChild("tip", tip);
    }

    public LifecycleLog lifecycle() {
        return lifecycleLog;
    }

    public LibRegistry libs() {
        return libs;
    }

    public NodeFixture root() {
        return root;
    }

    public BoxNodeFixture slots() {
        return slots;
    }

    public BoxNodeFixture tip() {
        return tip;
    }

    public Graphics graphics() {
        return graphics;
    }

    public EventBus events() {
        return events;
    }

    public TweenTool tween() {
        return tween;
    }

    /** 记录生命周期调用顺序。 */
    public static final class LifecycleLog {

        private final List<String> entries = new ArrayList<>();

        public void add(String entry) {
            entries.add(entry);
        }

        public List<String> entries() {
            return List.copyOf(entries);
        }
    }

    /** 可记录释放次数的宿主资源。 */
    public static final class TrackedDisposable implements DisposableFixture {

        private final String event;
        private final LifecycleLog lifecycleLog;
        private int disposeCount;

        public TrackedDisposable(String event, LifecycleLog lifecycleLog) {
            this.event = event;
            this.lifecycleLog = lifecycleLog;
        }

        @Override
        public void dispose() {
            disposeCount++;
            lifecycleLog.add(event);
        }

        public int getDisposeCount() {
            return disposeCount;
        }
    }

    /** 原生库注册表。 */
    public static final class LibRegistry {

        private final Map<String, ZeusLibFixture> libraries = new LinkedHashMap<>();
        private String lastRequestedId;

        public void register(ZeusLibFixture library) {
            libraries.put(library.id(), library);
        }

        public ZeusLibFixture get(String id) {
            lastRequestedId = id;
            return libraries.get(id);
        }

        public String getLastRequestedId() {
            return lastRequestedId;
        }
    }

    /** 动态组件入口。 */
    public static final class Graphics {

        private final BuilderEntry builderEntry;

        private Graphics(LifecycleLog lifecycleLog) {
            builderEntry = new BuilderEntry(lifecycleLog);
        }

        public BuilderEntry builder() {
            return builderEntry;
        }

        public BuilderEntry state() {
            return builderEntry;
        }
    }

    /** 模拟 {@code graphics.builder().box()}。 */
    public static final class BuilderEntry {

        private final LifecycleLog lifecycleLog;
        private BoxBuilder lastBoxBuilder;
        private int boxCallCount;

        private BuilderEntry(LifecycleLog lifecycleLog) {
            this.lifecycleLog = lifecycleLog;
        }

        public BoxBuilder box() {
            boxCallCount++;
            lastBoxBuilder = new BoxBuilder(lifecycleLog);
            return lastBoxBuilder;
        }

        public BoxBuilder getLastBoxBuilder() {
            return lastBoxBuilder;
        }

        public int getBoxCallCount() {
            return boxCallCount;
        }
    }

    /** 保留链式语法的非空 Box builder。 */
    public static final class BoxBuilder {

        private final LifecycleLog lifecycleLog;
        private String name;
        private boolean visible = true;
        private double width;
        private double height;
        private NodeFixture parent;
        private BoxNodeFixture attachedNode;

        private BoxBuilder(LifecycleLog lifecycleLog) {
            this.lifecycleLog = lifecycleLog;
        }

        public BoxBuilder name(String value) {
            name = value;
            return this;
        }

        public BoxBuilder visible(boolean value) {
            visible = value;
            return this;
        }

        public BoxBuilder width(double value) {
            width = value;
            return this;
        }

        public BoxBuilder height(double value) {
            height = value;
            return this;
        }

        public BoxBuilder parent(NodeFixture value) {
            parent = value;
            return this;
        }

        public BoxNodeFixture attach() {
            attachedNode = new BoxNodeFixture(name);
            attachedNode.setLifecycleLog(lifecycleLog);
            return attachedNode;
        }

        public String getName() {
            return name;
        }

        public boolean isVisible() {
            return visible;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public NodeFixture getParent() {
            return parent;
        }

        public BoxNodeFixture getAttachedNode() {
            return attachedNode;
        }
    }

    /** 全局事件总线。 */
    public static final class EventBus {

        private final LifecycleLog lifecycleLog;
        private EventListenerFixture listener;
        private String key;
        private TrackedDisposable handle;

        private EventBus(LifecycleLog lifecycleLog) {
            this.lifecycleLog = lifecycleLog;
        }

        public DisposableFixture on(String eventKey, EventListenerFixture eventListener) {
            key = eventKey;
            listener = eventListener;
            handle = new TrackedDisposable("event.dispose", lifecycleLog);
            return handle;
        }

        public void emit(String eventKey, Object event) {
            if (listener == null || !eventKey.equals(key)) {
                return;
            }
            listener.onEvent(eventKey, event);
        }

        public String getListenerClassName() {
            if (listener == null) {
                return null;
            }
            return listener.getClass().getName();
        }

        public TrackedDisposable getHandle() {
            return handle;
        }
    }

    /** Tween 宿主工具。 */
    public static final class TweenTool {

        private final LifecycleLog lifecycleLog;
        private NodeFixture target;
        private TweenPropsFixture props;
        private double duration;
        private Runnable complete;
        private int clearCount;

        private TweenTool(LifecycleLog lifecycleLog) {
            this.lifecycleLog = lifecycleLog;
        }

        public void to(NodeFixture tweenTarget, TweenPropsFixture tweenProps, double durationTicks, Runnable completion) {
            target = tweenTarget;
            props = tweenProps;
            duration = durationTicks;
            complete = completion;
        }

        public boolean clearAll(NodeFixture tweenTarget) {
            if (target != tweenTarget) {
                return false;
            }
            clearCount++;
            complete = null;
            lifecycleLog.add("tween.clearAll");
            return true;
        }

        public void finish() {
            if (complete == null) {
                throw new IllegalStateException("Tween completion is not installed");
            }
            Runnable callback = complete;
            complete = null;
            callback.run();
        }

        public NodeFixture getTarget() {
            return target;
        }

        public TweenPropsFixture getProps() {
            return props;
        }

        public double getDuration() {
            return duration;
        }

        public String getCompletionClassName() {
            if (complete == null) {
                return null;
            }
            return complete.getClass().getName();
        }

        public int getClearCount() {
            return clearCount;
        }
    }
}
