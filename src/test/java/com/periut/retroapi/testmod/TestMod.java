package com.periut.retroapi.testmod;

import com.periut.retroapi.api.*;
import net.minecraft.block.Block;
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

	public static Block TEST_BLOCK;
	public static Block COLOR_BLOCK;
	public static Block PIPE_BLOCK;
	public static Item TEST_ITEM;

	@Override
	public void init() {
		LOGGER.info("RetroAPI Test Mod initializing");

		TEST_BLOCK = ((RetroBlockAccess) RetroBlockAccess.create(Material.STONE))
			.retroapi$setSounds(Block.STONE_SOUNDS)
			.retroapi$setStrength(1.5f)
			.retroapi$setBlastResistance(10.0f)
			.retroapi$texture(new RetroIdentifier("retroapi_test", "test_block"))
			.retroapi$register(new RetroIdentifier("retroapi_test", "test_block"));
		TEST_BLOCK.setKey("testBlock");

		COLOR_BLOCK = ((RetroBlockAccess) new ColorBlock(Material.STONE))
			.retroapi$setSounds(Block.STONE_SOUNDS)
			.retroapi$setStrength(1.5f)
			.retroapi$setBlastResistance(10.0f)
			.retroapi$setRenderType(RenderTypes.BLOCK)
			.retroapi$register(new RetroIdentifier("retroapi_test", "color_block"));
		COLOR_BLOCK.setKey("colorBlock");

		PIPE_BLOCK = ((RetroBlockAccess) RetroBlockAccess.create(Material.STONE))
			.retroapi$setSounds(Block.METAL_SOUNDS)
			.retroapi$setStrength(2.0f)
			.retroapi$setSolidRender(false)
			.retroapi$setBlockBounds(4 / 16.0F, 4 / 16.0F, 4 / 16.0F, 12 / 16.0F, 12 / 16.0F, 12 / 16.0F)
			.retroapi$setRenderType(PIPE_RENDER_TYPE)
			.retroapi$register(new RetroIdentifier("retroapi_test", "pipe"));
		PIPE_BLOCK.setKey("pipeBlock");
		((RetroBlockAccess) PIPE_BLOCK).retroapi$setSprite(4);

		TEST_ITEM = RetroItemAccess.create()
			.setMaxStackSize(64)
			.setKey("testItem");
		((RetroItemAccess) TEST_ITEM)
			.retroapi$texture(new RetroIdentifier("retroapi_test", "test_item"))
			.retroapi$register(new RetroIdentifier("retroapi_test", "test_item"));

		LOGGER.info("Test block, color block, pipe block, and item registered");
	}
}
