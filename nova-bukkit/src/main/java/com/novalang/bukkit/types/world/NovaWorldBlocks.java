package com.novalang.bukkit.types.world;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** World、区块、方块及相关对象扩展的领域聚合器。 */
public final class NovaWorldBlocks {

    private NovaWorldBlocks() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaWorldExtra.register(builder);
        NovaWorldEntityOperations.register(builder);
        NovaWorldEnvironmentOperations.register(builder);
        NovaWorldParticleOperations.register(builder);
        NovaChunk.register(builder);
        NovaBlock.register(builder);
        NovaBlockFace.register(builder);
        NovaBukkitRegistrar.register(builder, NovaBlockStringTypes.class, NovaBlockStringTypes::register);
        NovaBlockState.register(builder);
        NovaMaterialData.register(builder);
        NovaLegacyCauldron.register(builder);
        NovaLegacyCrops.register(builder);
        NovaLegacyLever.register(builder);
        NovaLegacyRails.register(builder);
        NovaLegacyPistonBaseMaterial.register(builder);
        NovaLegacyDiode.register(builder);
        NovaLegacyPressurePlate.register(builder);
        NovaLegacyDirectional.register(builder);
        NovaNote.register(builder);
        NovaNoteBlock.register(builder);
        NovaMap.register(builder);
        NovaBukkitRegistrar.register(builder, NovaMapCanvas.class, NovaMapCanvas::register);
        NovaBukkitRegistrar.register(builder, NovaMapCursor.class, NovaMapCursor::register);
        NovaBukkitRegistrar.register(builder, NovaMapCursorCollection.class, NovaMapCursorCollection::register);
        NovaBukkitRegistrar.register(builder, NovaMapRenderer.class, NovaMapRenderer::register);
        NovaGenerator.register(builder);
        NovaBlockIterator.register(builder);
        NovaBukkitRegistrar.register(builder, NovaContainer.class, NovaContainer::register);
        NovaBukkitRegistrar.register(builder, NovaFurnace.class, NovaFurnace::register);
        NovaBukkitRegistrar.register(builder, NovaBrewingStand.class, NovaBrewingStand::register);
        NovaBukkitRegistrar.register(builder, NovaJukebox.class, NovaJukebox::register);
        NovaBukkitRegistrar.register(builder, NovaSign.class, NovaSign::register);
        NovaBukkitRegistrar.register(builder, NovaBeacon.class, NovaBeacon::register);
        NovaBukkitRegistrar.register(builder, NovaCommandBlock.class, NovaCommandBlock::register);
        NovaBukkitRegistrar.register(builder, NovaCreatureSpawner.class, NovaCreatureSpawner::register);
    }
}
