package com.periut.retroapi.testmod;

import com.periut.retroapi.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("RetroAPI Test");

	public static final RetroIdentifier PIPE_RENDER_TYPE = RenderType.register(
		new RetroIdentifier("retroapi_test", "pipe"),
		ctx -> {
			ctx.renderAllLitFaces(4);
			return true;
		}
	);

	public static final RetroBlockEntityType<CrateBlockEntity> CRATE_TYPE =
		new RetroBlockEntityType<>(
			new RetroIdentifier("retroapi_test", "crate"),
			CrateBlockEntity.class, CrateBlockEntity::new
		);

	public static Block TEST_BLOCK;
	public static Block COLOR_BLOCK;
	public static Block PIPE_BLOCK;
	public static Block CRATE_BLOCK;
	public static Item TEST_ITEM;

	@Override
	public void init() {
		LOGGER.info("RetroAPI Test Mod initializing");

		TEST_BLOCK = RetroBlockAccess.create(Material.STONE)
			.setSounds(Block.STONE_SOUNDS)
			.setStrength(1.5f)
			.setBlastResistance(10.0f);
		((RetroBlockAccess) TEST_BLOCK)
			.retroapi$texture(new RetroIdentifier("retroapi_test", "test_block"))
			.register(new RetroIdentifier("retroapi_test", "test_block"));
		TEST_BLOCK.setKey("testBlock");

		COLOR_BLOCK = new ColorBlock(Material.STONE)
			.setSounds(Block.STONE_SOUNDS)
			.setStrength(1.5f)
			.setBlastResistance(10.0f);
		((RetroBlockAccess) COLOR_BLOCK)
			.setRenderType(RenderTypes.BLOCK)
			.register(new RetroIdentifier("retroapi_test", "color_block"));
		COLOR_BLOCK.setKey("colorBlock");

		PIPE_BLOCK = RetroBlockAccess.create(Material.STONE)
			.setSounds(Block.METAL_SOUNDS)
			.setStrength(2.0f);
		((RetroBlockAccess) PIPE_BLOCK)
			.retroapi$setSolidRender(false)
			.retroapi$setBlockBounds(4 / 16.0F, 4 / 16.0F, 4 / 16.0F, 12 / 16.0F, 12 / 16.0F, 12 / 16.0F)
			.setRenderType(PIPE_RENDER_TYPE)
			.register(new RetroIdentifier("retroapi_test", "pipe"));
		PIPE_BLOCK.setKey("pipeBlock");
		((RetroBlockAccess) PIPE_BLOCK).retroapi$setSprite(4);

		CRATE_BLOCK = RetroBlockAccess.create(Material.WOOD)
			.setSounds(Block.WOOD_SOUNDS)
			.setStrength(2.5f);
		((RetroBlockAccess) CRATE_BLOCK)
			.setBlockEntity(CRATE_TYPE)
			.setActivated((world, x, y, z, player) -> {
				BlockEntity be = world.getBlockEntity(x, y, z);
				if (be instanceof CrateBlockEntity crate) {
					player.openChestMenu(crate);
				}
				return true;
			})
			.retroapi$texture(new RetroIdentifier("retroapi_test", "crate"))
			.register(new RetroIdentifier("retroapi_test", "crate"));
		CRATE_BLOCK.setKey("crateBlock");

		TEST_ITEM = RetroItemAccess.create()
			.setMaxStackSize(64)
			.setKey("testItem");
		((RetroItemAccess) TEST_ITEM)
			.retroapi$texture(new RetroIdentifier("retroapi_test", "test_item"))
			.retroapi$register(new RetroIdentifier("retroapi_test", "test_item"));

		LOGGER.info("Registered test_block, color_block, pipe, crate, test_item");
	}
}
