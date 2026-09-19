package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

/** Bukkit 1.12.2 BossBar 别名；BossBar 的创建由 Server.createBossBar 提供。 */
@Requires(classes = {"org.bukkit.boss.BossBar"})
final class NovaBossBar {

    private NovaBossBar() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(BossBar.class, "title", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).getTitle()));
        b.extension(BossBar.class, "color", f -> f.returns(BarColor.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).getColor()));
        b.extension(BossBar.class, "style", f -> f.returns(BarStyle.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).getStyle()));
        b.extension(BossBar.class, "progress", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).getProgress()));
        b.extension(BossBar.class, "players", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Player.class))).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).getPlayers()));
        b.extension(BossBar.class, "isVisible", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).isVisible()));
        b.extension(BossBar.class, "setTitle", f -> f.param("title", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).setTitle(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(BossBar.class, "setColor", f -> f.param("color", BarColor.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).setColor(NovaTypeSupport.argument(a, 1, BarColor.class)); return null; }));
        b.extension(BossBar.class, "setColor", f -> f.param("color", String.class).invoke(NovaBossBar::setColor));
        b.extension(BossBar.class, "setStyle", f -> f.param("style", BarStyle.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).setStyle(NovaTypeSupport.argument(a, 1, BarStyle.class)); return null; }));
        b.extension(BossBar.class, "setStyle", f -> f.param("style", String.class).invoke(NovaBossBar::setStyle));
        b.extension(BossBar.class, "setProgress", f -> f.param("progress", Double.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).setProgress(NovaTypeSupport.argument(a, 1, Double.class)); return null; }));
        b.extension(BossBar.class, "addPlayer", f -> f.param("player", Player.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).addPlayer(NovaTypeSupport.argument(a, 1, Player.class)); return null; }));
        b.extension(BossBar.class, "removePlayer", f -> f.param("player", Player.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).removePlayer(NovaTypeSupport.argument(a, 1, Player.class)); return null; }));
        b.extension(BossBar.class, "removeAll", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).removeAll(); return null; }));
        b.extension(BossBar.class, "setVisible", f -> f.param("visible", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).setVisible(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        b.extension(BossBar.class, "show", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).show(); return null; }));
        b.extension(BossBar.class, "hide", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).hide(); return null; }));
        b.extension(BossBar.class, "addFlag", f -> f.param("flag", BarFlag.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).addFlag(NovaTypeSupport.argument(a, 1, BarFlag.class)); return null; }));
        b.extension(BossBar.class, "addFlag", f -> f.param("flag", String.class).invoke(NovaBossBar::addFlag));
        b.extension(BossBar.class, "removeFlag", f -> f.param("flag", BarFlag.class).invoke(a -> { NovaTypeSupport.argument(a, 0, BossBar.class).removeFlag(NovaTypeSupport.argument(a, 1, BarFlag.class)); return null; }));
        b.extension(BossBar.class, "removeFlag", f -> f.param("flag", String.class).invoke(NovaBossBar::removeFlag));
        b.extension(BossBar.class, "hasFlag", f -> f.param("flag", BarFlag.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, BossBar.class).hasFlag(NovaTypeSupport.argument(a, 1, BarFlag.class))));
        b.extension(BossBar.class, "hasFlag", f -> f.param("flag", String.class).returns(Boolean.class).invoke(NovaBossBar::hasFlag));
    }

    private static Object setColor(Object[] arguments) {
        bossBar(arguments).setColor(enumValue(BarColor.class, arguments, 1));
        return null;
    }

    private static Object setStyle(Object[] arguments) {
        bossBar(arguments).setStyle(enumValue(BarStyle.class, arguments, 1));
        return null;
    }

    private static Object addFlag(Object[] arguments) {
        bossBar(arguments).addFlag(enumValue(BarFlag.class, arguments, 1));
        return null;
    }

    private static Object removeFlag(Object[] arguments) {
        bossBar(arguments).removeFlag(enumValue(BarFlag.class, arguments, 1));
        return null;
    }

    private static Boolean hasFlag(Object[] arguments) {
        return bossBar(arguments).hasFlag(enumValue(BarFlag.class, arguments, 1));
    }

    private static BossBar bossBar(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BossBar.class);
    }

    private static <T extends Enum<T>> T enumValue(Class<T> enumClass, Object[] arguments, int index) {
        T value = NovaTypeSupport.findEnum(enumClass, NovaTypeSupport.argument(arguments, index, String.class));
        if (value == null) {
            throw new IllegalArgumentException("BossBar 枚举值不存在");
        }
        return value;
    }
}
