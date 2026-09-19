/**
 * Nova Runtime Workspace 公共 API。
 *
 * <p>该包提供严格的 Workspace 配置、跨目录模块图、Source Map、隔离字节码执行、
 * 稳定回调和树形资源销毁能力。RuntimeWorkspace 是一次性对象，不包含 reload 状态；
 * 上层业务通过销毁旧实例并创建新实例完成重载。</p>
 */
package com.novalang.workspace;
