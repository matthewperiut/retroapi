package com.periut.retroapi.testmod;

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

	public static Block TEST_BLOCK;
	public static Item TEST_ITEM;

	@Override
	public void init() {
		LOGGER.info("RetroAPI Test Mod initializing");

		TEST_BLOCK = RetroBlock.create(new RetroIdentifier("retroapi_test", "test_block"))
			.material(Material.STONE)
			.hardness(1.5f)
			.resistance(10.0f)
			.sounds(Block.STONE_SOUNDS)
			.translationKey("testBlock")
			.build();

		TEST_ITEM = RetroItem.create(new RetroIdentifier("retroapi_test", "test_item"))
			.maxStackSize(64)
			.translationKey("testItem")
			.build();

		LOGGER.info("Test block and item registered");
	}
}
