package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Spellcaster;

/** Spigot 1.12.2 Spellcaster 扩展。 */
@Requires(classes = {"org.bukkit.entity.Spellcaster", "org.bukkit.entity.Spellcaster$Spell"})
public final class NovaSpellcaster {

    private NovaSpellcaster() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Spellcaster.class, "spell", function -> function.returns(Spellcaster.Spell.class).invoke(arguments -> caster(arguments).getSpell()));
        builder.extension(Spellcaster.class, "setSpell", function -> function.param("spell", Spellcaster.Spell.class).returns(Void.TYPE).invoke(arguments -> {
            caster(arguments).setSpell(NovaTypeSupport.argument(arguments, 1, Spellcaster.Spell.class));
            return null;
        }));
        builder.extension(Spellcaster.class, "setSpell", function -> function.param("spell", String.class).returns(Void.TYPE).invoke(NovaSpellcaster::setSpell));
    }

    private static Object setSpell(Object[] arguments) {
        Spellcaster.Spell spell = NovaTypeSupport.findEnum(Spellcaster.Spell.class, NovaTypeSupport.argument(arguments, 1, String.class));
        if (spell == null) {
            throw new IllegalArgumentException("Spellcaster 枚举值不存在");
        }
        caster(arguments).setSpell(spell);
        return null;
    }

    private static Spellcaster caster(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Spellcaster.class);
    }
}
