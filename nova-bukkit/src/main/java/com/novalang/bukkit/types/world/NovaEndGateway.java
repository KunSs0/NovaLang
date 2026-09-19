package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.block.EndGateway;

@Requires(classes = {"org.bukkit.block.EndGateway"})
public final class NovaEndGateway {
    private NovaEndGateway() { }
    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        b.extension(EndGateway.class, "exitLocation", f -> f.returns(nullableLocation).invoke(a -> e(a).getExitLocation()));
        b.extension(EndGateway.class, "setExitLocation", f -> f.param("location", nullableLocation).returns(Void.TYPE).invoke(a -> { e(a).setExitLocation(NovaTypeSupport.argument(a, 1, Location.class)); return null; }));
        b.extension(EndGateway.class, "isExactTeleport", f -> f.returns(Boolean.class).invoke(a -> e(a).isExactTeleport()));
        b.extension(EndGateway.class, "setExactTeleport", f -> f.param("exact", Boolean.class).returns(Void.TYPE).invoke(a -> { e(a).setExactTeleport(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
    }
    private static EndGateway e(Object[] a) { return NovaTypeSupport.argument(a, 0, EndGateway.class); }
}
