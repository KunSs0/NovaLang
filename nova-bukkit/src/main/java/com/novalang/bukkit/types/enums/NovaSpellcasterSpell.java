package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Spellcaster;

/** Spigot 1.12.2 Spellcaster.Spell 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.Spellcaster$Spell"})
final class NovaSpellcasterSpell {

    private NovaSpellcasterSpell() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "spellcasterSpell", Spellcaster.Spell.class);
    }
}
