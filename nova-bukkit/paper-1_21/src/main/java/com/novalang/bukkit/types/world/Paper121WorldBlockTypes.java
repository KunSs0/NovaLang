package com.novalang.bukkit.types.world;

import com.novalang.runtime.host.JavaTypes;

/** Paper 1.21.x BlockData 类型扩展聚合注册器。 */
public final class Paper121WorldBlockTypes {

    private Paper121WorldBlockTypes() {
    }

    /** 注册 Paper 1.21.x 的 BlockData 类型扩展。 */
    public static void register(JavaTypes.Builder builder) {
        NovaBlockDataAccess.register(builder);
        NovaBlockFaceAttachable.register(builder);
        NovaBlockAgeable.register(builder);
        NovaBlockAnaloguePowerable.register(builder);
        NovaBlockAttachable.register(builder);
        NovaBlockBamboo.register(builder);
        NovaBlockBed.register(builder);
        NovaBlockBeehive.register(builder);
        NovaBlockBell.register(builder);
        NovaBlockBigDripleaf.register(builder);
        NovaBlockBisected.register(builder);
        NovaBlockBrewingStand.register(builder);
        NovaBlockBrushable.register(builder);
        NovaBlockBubbleColumn.register(builder);
        NovaBlockCake.register(builder);
        NovaBlockCampfire.register(builder);
        NovaBlockCandle.register(builder);
        NovaBlockCaveVinesPlant.register(builder);
        NovaBlockChest.register(builder);
        NovaBlockChiseledBookshelf.register(builder);
        NovaBlockCommandBlock.register(builder);
        NovaBlockComparator.register(builder);
        NovaBlockConduit.register(builder);
        NovaBlockCrafter.register(builder);
        NovaBlockData.register(builder);
        NovaBlockDaylightDetector.register(builder);
        NovaBlockDirectional.register(builder);
        NovaBlockDispenser.register(builder);
        NovaBlockDoor.register(builder);
        NovaBlockEndPortalFrame.register(builder);
        NovaBlockEntityStorage.register(builder);
        NovaBlockFarmland.register(builder);
        NovaBlockGate.register(builder);
        NovaBlockHangable.register(builder);
        NovaBlockHatchable.register(builder);
        NovaBlockHopper.register(builder);
        NovaBlockJigsaw.register(builder);
        NovaBlockJukebox.register(builder);
        NovaBlockLeaves.register(builder);
        NovaBlockLectern.register(builder);
        NovaBlockLevelled.register(builder);
        NovaBlockLidded.register(builder);
        NovaBlockLightable.register(builder);
        NovaBlockMultipleFacing.register(builder);
        NovaBlockNote.register(builder);
        NovaBlockOpenable.register(builder);
        NovaBlockOrientable.register(builder);
        NovaBlockPinkPetals.register(builder);
        NovaBlockPiston.register(builder);
        NovaBlockPistonHead.register(builder);
        NovaBlockPointedDripstone.register(builder);
        NovaBlockPowerable.register(builder);
        NovaBlockRail.register(builder);
        NovaBlockRedstoneWire.register(builder);
        NovaBlockRepeater.register(builder);
        NovaBlockRespawnAnchor.register(builder);
        NovaBlockRotatable.register(builder);
        NovaBlockSapling.register(builder);
        NovaBlockScaffolding.register(builder);
        NovaBlockSculkCatalyst.register(builder);
        NovaBlockSculkSensor.register(builder);
        NovaBlockSculkShrieker.register(builder);
        NovaBlockSeaPickle.register(builder);
        NovaBlockSignSide.register(builder);
        NovaBlockSlab.register(builder);
        NovaBlockSnow.register(builder);
        NovaBlockSnowable.register(builder);
        NovaBlockSpawnerEntry.register(builder);
        NovaBlockSpawnerEquipment.register(builder);
        NovaBlockSpawnRule.register(builder);
        NovaBlockStairs.register(builder);
        NovaBlockStructure.register(builder);
        NovaBlockStructureBlock.register(builder);
        NovaBlockSwitch.register(builder);
        NovaBlockTechnicalPiston.register(builder);
        NovaBlockTileState.register(builder);
        NovaBlockTNT.register(builder);
        NovaBlockTrialSpawner.register(builder);
        NovaBlockTripwire.register(builder);
        NovaBlockTurtleEgg.register(builder);
        NovaBlockType.register(builder);
        NovaBlockVault.register(builder);
        NovaBlockWall.register(builder);
        NovaBlockWaterlogged.register(builder);
    }
}
