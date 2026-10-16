package com.novalang.runtime.compilation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 一个单入口 Nova 程序的依赖优先编译计划。 */
final class CompilationPlan {
    private final List<Group> groups;

    /**
     * 复制模块编译顺序。
     * @param groups 依赖优先的模块编译组
     */
    CompilationPlan(List<Group> groups) {
        this.groups = Collections.unmodifiableList(new ArrayList<>(groups));
    }

    /** @return 依赖优先且每个模块仅出现一次的编译组列表 */
    List<Group> getGroups() {
        return groups;
    }

    /** 一个模块的生成包名、源码及传递链接依赖。 */
    static final class Group {
        private final String id;
        private final String packageName;
        private final List<String> moduleIds;
        private final List<Group> dependencies;

        /**
         * 保存本组的不可变链接信息。
         * @param id 稳定编译组标识
         * @param packageName 生成字节码的包名
         * @param moduleIds 本组模块标识
         * @param dependencies 按拓扑顺序排列的传递依赖
         */
        Group(String id, String packageName, List<String> moduleIds, List<Group> dependencies) {
            this.id = id;
            this.packageName = packageName;
            this.moduleIds = Collections.unmodifiableList(new ArrayList<>(moduleIds));
            this.dependencies = Collections.unmodifiableList(new ArrayList<>(dependencies));
        }

        /** @return 稳定编译组标识 */
        String getId() {
            return id;
        }

        /** @return 生成类所属包名 */
        String getPackageName() {
            return packageName;
        }

        /** @return 本组模块标识 */
        List<String> getModuleIds() {
            return moduleIds;
        }

        /** @return 按拓扑顺序排列的传递依赖 */
        List<Group> getDependencies() {
            return dependencies;
        }
    }
}
