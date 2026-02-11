package com.periut.retroapi.testmod;

import com.periut.retroapi.api.RenderType;
import com.periut.retroapi.api.RetroBlock;
import com.periut.retroapi.api.RetroIdentifier;
import com.periut.retroapi.api.RetroItem;
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

		TEST_BLOCK = new RetroBlock(Material.STONE)
			.setSounds(Block.STONE_SOUNDS)
			.setStrength(1.5f)
			.setBlastResistance(10.0f)
			.setKey("testBlock")
			.texture(new RetroIdentifier("retroapi_test", "test_block"))
			.register(new RetroIdentifier("retroapi_test", "test_block"));

		COLOR_BLOCK = new ColorBlock(Material.STONE)
			.setSounds(Block.STONE_SOUNDS)
			.setStrength(1.5f)
			.setBlastResistance(10.0f)
			.setKey("colorBlock")
			.register(new RetroIdentifier("retroapi_test", "color_block"));

		PIPE_BLOCK = new RetroBlock(Material.STONE)
			.setSounds(Block.METAL_SOUNDS)
			.setStrength(2.0f)
			.setKey("pipeBlock")
			.setSolidRender(false)
			.setBlockBounds(4 / 16.0F, 4 / 16.0F, 4 / 16.0F, 12 / 16.0F, 12 / 16.0F, 12 / 16.0F)
			.setRenderType(PIPE_RENDER_TYPE)
			.register(new RetroIdentifier("retroapi_test", "pipe"));
		((RetroBlock) PIPE_BLOCK).setSprite(4);

		TEST_ITEM = new RetroItem()
			.setMaxStackSize(64)
			.setKey("testItem")
			.texture(new RetroIdentifier("retroapi_test", "test_item"))
			.register(new RetroIdentifier("retroapi_test", "test_item"));

		LOGGER.info("Test block, color block, pipe block, and item registered");
	}
}
