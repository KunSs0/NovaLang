package com.novalang.bukkit.types.world;

import com.novalang.runtime.host.JavaTypes;
import com.novalang.bukkit.types.event.NovaBlockPhysicsEvent;

/**
 * Bukkit 1.12 旧版材质数据扩展的聚合注册器。
 *
 * <p>这些类型依赖 {@code org.bukkit.material} 旧 API，必须与 Bukkit 1.12
 * API 一起编译和加载，不能放入跨版本 Core。</p>
 */
public final class Bukkit112LegacyWorldTypes {

    private Bukkit112LegacyWorldTypes() {
    }

    /** 注册全部 Bukkit 1.12 旧版材质数据类型。 */
    public static void register(JavaTypes.Builder builder) {
        NovaBlock.register(builder);
        NovaBlockPhysicsEvent.register(builder);
        NovaWorldExtra.register(builder);
        NovaWorldEntityOperations.register(builder);
        NovaWorldEnvironmentOperations.register(builder);
        NovaWorldParticleOperations.register(builder);
        NovaChunk.register(builder);
        NovaMaterialData.register(builder);
        NovaLegacyColorable.register(builder);
        NovaLegacyOpenable.register(builder);
        NovaLegacyAttachable.register(builder);
        NovaLegacyTexturedMaterial.register(builder);
        NovaLegacyRedstone.register(builder);
        NovaLegacyPressureSensor.register(builder);
        NovaLegacyFurnaceAndDispenser.register(builder);
        NovaLegacySimpleAttachableMaterialData.register(builder);
        NovaLegacyPoweredRail.register(builder);
        NovaLegacyDetectorRail.register(builder);
        NovaLegacyComparator.register(builder);
        NovaLegacyObserver.register(builder);
        NovaLegacyTripwire.register(builder);
        NovaLegacyTripwireHook.register(builder);
        NovaLegacyButton.register(builder);
        NovaLegacyGate.register(builder);
        NovaLegacyHopper.register(builder);
        NovaLegacyDirectionalContainer.register(builder);
        NovaLegacyExtendedRails.register(builder);
        NovaLegacyFurnace.register(builder);
        NovaLegacyDispenserMaterial.register(builder);
        NovaLegacyDoor.register(builder);
        NovaLegacyTrapDoor.register(builder);
        NovaLegacyStairs.register(builder);
        NovaLegacyStep.register(builder);
        NovaLegacyWoodenStep.register(builder);
        NovaLegacyTorch.register(builder);
        NovaLegacyRedstoneTorch.register(builder);
        NovaLegacyRedstoneWire.register(builder);
        NovaLegacyLeaves.register(builder);
        NovaLegacySapling.register(builder);
        NovaLegacyWood.register(builder);
        NovaLegacyTree.register(builder);
        NovaLegacyVine.register(builder);
        NovaLegacyCocoaPlant.register(builder);
        NovaLegacyCake.register(builder);
        NovaLegacyChestMaterial.register(builder);
        NovaLegacyEnderChestMaterial.register(builder);
        NovaLegacyFlowerPot.register(builder);
        NovaLegacyLadder.register(builder);
        NovaLegacySignMaterial.register(builder);
        NovaLegacySkullMaterial.register(builder);
        NovaLegacyPumpkin.register(builder);
        NovaLegacyMushroom.register(builder);
        NovaLegacyLongGrass.register(builder);
        NovaLegacyNetherWarts.register(builder);
        NovaLegacyWool.register(builder);
        NovaLegacyDye.register(builder);
        NovaLegacyCoal.register(builder);
        NovaLegacyMonsterEggs.register(builder);
        NovaLegacySpawnEgg.register(builder);
        NovaLegacySmoothBrick.register(builder);
        NovaLegacySandstone.register(builder);
        NovaLegacyCommandMaterial.register(builder);
        NovaLegacyPistonExtensionMaterial.register(builder);
        NovaLegacyBed.register(builder);
        NovaLegacyBannerMaterial.register(builder);
        NovaLegacyCauldron.register(builder);
        NovaLegacyCrops.register(builder);
        NovaLegacyLever.register(builder);
        NovaLegacyRails.register(builder);
        NovaLegacyPistonBaseMaterial.register(builder);
        NovaLegacyDiode.register(builder);
        NovaLegacyPressurePlate.register(builder);
        NovaLegacyDirectional.register(builder);
    }
}
