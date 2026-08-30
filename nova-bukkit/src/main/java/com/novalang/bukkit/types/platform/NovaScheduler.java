package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

/** Bukkit scheduler/task 的 Fluxon 别名。 */
final class NovaScheduler {

    private NovaScheduler() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(BukkitTask.class, "taskId", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitTask.class).getTaskId()));
        builder.extension(BukkitTask.class, "owner", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitTask.class).getOwner()));
        builder.extension(BukkitTask.class, "isSync", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitTask.class).isSync()));
        builder.extension(BukkitTask.class, "isCancelled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitTask.class).isCancelled()));
        builder.extension(BukkitTask.class, "cancel", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, BukkitTask.class).cancel(); return null; }));
        builder.extension(BukkitWorker.class, "taskId", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitWorker.class).getTaskId()));
        builder.extension(BukkitWorker.class, "owner", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitWorker.class).getOwner()));
        builder.extension(BukkitWorker.class, "thread", f -> f.returns(Thread.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitWorker.class).getThread()));
        builder.extension(BukkitRunnable.class, "isCancelled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitRunnable.class).isCancelled()));
        builder.extension(BukkitRunnable.class, "cancel", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, BukkitRunnable.class).cancel(); return null; }));
        builder.extension(BukkitRunnable.class, "getTaskId", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitRunnable.class).getTaskId()));

        builder.extension(BukkitScheduler.class, "scheduleSyncDelayedTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).param("delay", Long.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleSyncDelayedTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class), NovaTypeSupport.argument(a, 3, Long.class))));
        builder.extension(BukkitScheduler.class, "scheduleSyncDelayedTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleSyncDelayedTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class))));
        builder.extension(BukkitScheduler.class, "scheduleSyncRepeatingTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).param("delay", Long.class).param("period", Long.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleSyncRepeatingTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class), NovaTypeSupport.argument(a, 3, Long.class), NovaTypeSupport.argument(a, 4, Long.class))));
        builder.extension(BukkitScheduler.class, "scheduleAsyncDelayedTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).param("delay", Long.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleAsyncDelayedTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class), NovaTypeSupport.argument(a, 3, Long.class))));
        builder.extension(BukkitScheduler.class, "scheduleAsyncDelayedTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleAsyncDelayedTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class))));
        builder.extension(BukkitScheduler.class, "scheduleAsyncRepeatingTask", f -> f.param("plugin", Plugin.class).param("task", Runnable.class).param("delay", Long.class).param("period", Long.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).scheduleAsyncRepeatingTask(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Runnable.class), NovaTypeSupport.argument(a, 3, Long.class), NovaTypeSupport.argument(a, 4, Long.class))));
        builder.extension(BukkitScheduler.class, "cancelTask", f -> f.param("taskId", Integer.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BukkitScheduler.class).cancelTask(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(BukkitScheduler.class, "cancelTasks", f -> f.param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BukkitScheduler.class).cancelTasks(NovaTypeSupport.argument(a, 1, Plugin.class)); return null; }));
        builder.extension(BukkitScheduler.class, "isCurrentlyRunning", f -> f.param("taskId", Integer.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).isCurrentlyRunning(NovaTypeSupport.argument(a, 1, Integer.class))));
        builder.extension(BukkitScheduler.class, "isQueued", f -> f.param("taskId", Integer.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).isQueued(NovaTypeSupport.argument(a, 1, Integer.class))));
        builder.extension(BukkitScheduler.class, "getActiveWorkers", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(BukkitWorker.class))).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).getActiveWorkers()));
        builder.extension(BukkitScheduler.class, "getPendingTasks", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(BukkitTask.class))).invoke(a -> NovaTypeSupport.argument(a, 0, BukkitScheduler.class).getPendingTasks()));
        registerSchedulerRunMethods(builder);

        registerRunnable(builder, "runTask", 0);
        registerRunnable(builder, "runTaskAsynchronously", 1);
        registerRunnable(builder, "runTaskLater", 2);
        registerRunnable(builder, "runTaskLaterAsynchronously", 3);
        registerRunnable(builder, "runTaskTimer", 4);
        registerRunnable(builder, "runTaskTimerAsynchronously", 5);
    }

    private static void registerSchedulerRunMethods(JavaTypes.Builder builder) {
        registerSchedulerRunMethod(builder, "runTask", 0, Runnable.class);
        registerSchedulerRunMethod(builder, "runTask", 0, BukkitRunnable.class);
        registerSchedulerRunMethod(builder, "runTaskAsynchronously", 1, Runnable.class);
        registerSchedulerRunMethod(builder, "runTaskAsynchronously", 1, BukkitRunnable.class);
        registerSchedulerRunMethod(builder, "runTaskLater", 2, Runnable.class);
        registerSchedulerRunMethod(builder, "runTaskLater", 2, BukkitRunnable.class);
        registerSchedulerRunMethod(builder, "runTaskLaterAsynchronously", 3, Runnable.class);
        registerSchedulerRunMethod(builder, "runTaskLaterAsynchronously", 3, BukkitRunnable.class);
        registerSchedulerRunMethod(builder, "runTaskTimer", 4, Runnable.class);
        registerSchedulerRunMethod(builder, "runTaskTimer", 4, BukkitRunnable.class);
        registerSchedulerRunMethod(builder, "runTaskTimerAsynchronously", 5, Runnable.class);
        registerSchedulerRunMethod(builder, "runTaskTimerAsynchronously", 5, BukkitRunnable.class);
    }

    private static void registerSchedulerRunMethod(JavaTypes.Builder builder,
                                                   String name,
                                                   int mode,
                                                   Class<? extends Runnable> taskType) {
        if (mode < 2) {
            builder.extension(BukkitScheduler.class, name, f -> f
                    .param("plugin", Plugin.class).param("task", taskType).returns(BukkitTask.class)
                    .invoke(a -> runScheduled(a, mode)));
            return;
        }
        if (mode < 4) {
            builder.extension(BukkitScheduler.class, name, f -> f
                    .param("plugin", Plugin.class).param("task", taskType).param("delay", Long.class)
                    .returns(BukkitTask.class).invoke(a -> runScheduled(a, mode)));
            return;
        }
        builder.extension(BukkitScheduler.class, name, f -> f
                .param("plugin", Plugin.class).param("task", taskType)
                .param("delay", Long.class).param("period", Long.class)
                .returns(BukkitTask.class).invoke(a -> runScheduled(a, mode)));
    }

    private static BukkitTask runScheduled(Object[] arguments, int mode) {
        BukkitScheduler scheduler = NovaTypeSupport.argument(arguments, 0, BukkitScheduler.class);
        Plugin plugin = NovaTypeSupport.argument(arguments, 1, Plugin.class);
        Runnable task = NovaTypeSupport.argument(arguments, 2, Runnable.class);
        if (mode == 0) {
            return scheduler.runTask(plugin, task);
        }
        if (mode == 1) {
            return scheduler.runTaskAsynchronously(plugin, task);
        }
        Long delay = NovaTypeSupport.argument(arguments, 3, Long.class);
        if (mode == 2) {
            return scheduler.runTaskLater(plugin, task, delay);
        }
        if (mode == 3) {
            return scheduler.runTaskLaterAsynchronously(plugin, task, delay);
        }
        Long period = NovaTypeSupport.argument(arguments, 4, Long.class);
        if (mode == 4) {
            return scheduler.runTaskTimer(plugin, task, delay, period);
        }
        return scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    private static void registerRunnable(JavaTypes.Builder b, String name, int mode) {
        if (mode < 2) {
            b.extension(BukkitRunnable.class, name, f -> f.param("plugin", Plugin.class).returns(BukkitTask.class).invoke(a -> {
                BukkitRunnable task = NovaTypeSupport.argument(a, 0, BukkitRunnable.class);
                Plugin plugin = NovaTypeSupport.argument(a, 1, Plugin.class);
                if (mode == 1) {
                    return task.runTaskAsynchronously(plugin);
                }
                return task.runTask(plugin);
            }));
            return;
        }
        if (mode < 4) {
            b.extension(BukkitRunnable.class, name, f -> f.param("plugin", Plugin.class).param("delay", Long.class).returns(BukkitTask.class).invoke(a -> {
                BukkitRunnable task = NovaTypeSupport.argument(a, 0, BukkitRunnable.class);
                Plugin plugin = NovaTypeSupport.argument(a, 1, Plugin.class);
                Long delay = NovaTypeSupport.argument(a, 2, Long.class);
                if (mode == 3) {
                    return task.runTaskLaterAsynchronously(plugin, delay);
                }
                return task.runTaskLater(plugin, delay);
            }));
            return;
        }
        b.extension(BukkitRunnable.class, name, f -> f.param("plugin", Plugin.class).param("delay", Long.class).param("period", Long.class).returns(BukkitTask.class).invoke(a -> {
            BukkitRunnable task = NovaTypeSupport.argument(a, 0, BukkitRunnable.class);
            Plugin plugin = NovaTypeSupport.argument(a, 1, Plugin.class);
            Long delay = NovaTypeSupport.argument(a, 2, Long.class);
            Long period = NovaTypeSupport.argument(a, 3, Long.class);
            if (mode == 5) {
                return task.runTaskTimerAsynchronously(plugin, delay, period);
            }
            return task.runTaskTimer(plugin, delay, period);
        }));
    }
}
