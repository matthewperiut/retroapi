package com.periut.retroapi.testmod;

import com.periut.retroapi.register.RetroIdentifier;
import com.periut.retroapi.register.block.RetroBlockAccess;
import com.periut.retroapi.register.blockentity.MenuHelper;
import com.periut.retroapi.register.blockentity.RetroBlockEntityType;
import com.periut.retroapi.register.item.RetroItemAccess;
import com.periut.retroapi.register.rendertype.RenderType;
import com.periut.retroapi.register.rendertype.RenderTypes;
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

	public static final RetroBlockEntityType<FreezerBlockEntity> FREEZER_TYPE =
		new RetroBlockEntityType<>(
			new RetroIdentifier("retroapi_test", "freezer"),
			FreezerBlockEntity.class, FreezerBlockEntity::new
		);

	public static final RetroIdentifier FREEZER_SCREEN = new RetroIdentifier("retroapi_test", "freezer_screen");

	public static final int BLOCK_COUNT = 200;
	public static final Block[] BLOCKS = new Block[BLOCK_COUNT];
	public static final int ITEM_COUNT = 200;
	public static final Item[] ITEMS = new Item[ITEM_COUNT];

	public static Block TEST_BLOCK;
	public static Block COLOR_BLOCK;
	public static Block PIPE_BLOCK;
	public static Block CRATE_BLOCK;
	public static Block FREEZER_BLOCK;
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
					crate.openCount++;
					MenuHelper.open(player, new CrateMenu(player.inventory, crate), MenuHelper.CHEST, crate);
				}
				return true;
			})
			.retroapi$texture(new RetroIdentifier("retroapi_test", "crate"))
			.register(new RetroIdentifier("retroapi_test", "crate"));
		CRATE_BLOCK.setKey("crateBlock");

		FREEZER_BLOCK = RetroBlockAccess.create(Material.STONE)
			.setSounds(Block.STONE_SOUNDS)
			.setStrength(3.5f);
		((RetroBlockAccess) FREEZER_BLOCK)
			.setBlockEntity(FREEZER_TYPE)
			.setActivated((world, x, y, z, player) -> {
				BlockEntity be = world.getBlockEntity(x, y, z);
				if (be instanceof FreezerBlockEntity freezer) {
					MenuHelper.open(player, new FreezerMenu(player.inventory, freezer), MenuHelper.FURNACE, freezer, FREEZER_SCREEN);
				}
				return true;
			})
			.retroapi$texture(new RetroIdentifier("retroapi_test", "freezer"))
			.register(new RetroIdentifier("retroapi_test", "freezer"));
		FREEZER_BLOCK.setKey("freezerBlock");

		TEST_ITEM = RetroItemAccess.create()
			.setMaxStackSize(64)
			.setKey("testItem");
		((RetroItemAccess) TEST_ITEM)
			.retroapi$texture(new RetroIdentifier("retroapi_test", "test_item"))
			.retroapi$register(new RetroIdentifier("retroapi_test", "test_item"));

		for (int i = 0; i < BLOCK_COUNT; i++) {
			BLOCKS[i] = RetroBlockAccess.create(Material.STONE)
				.setSounds(Block.STONE_SOUNDS)
				.setStrength(1.5f);
			((RetroBlockAccess) BLOCKS[i])
				.register(new RetroIdentifier("retroapi_test", "block_" + i));
			((RetroBlockAccess) BLOCKS[i]).retroapi$setSprite(Block.COBBLESTONE.getSprite(0));
			BLOCKS[i].setKey("block" + i);
		}

		for (int i = 0; i < ITEM_COUNT; i++) {
			ITEMS[i] = RetroItemAccess.create()
				.setMaxStackSize(64)
				.setKey("item" + i);
			((RetroItemAccess) ITEMS[i])
				.retroapi$register(new RetroIdentifier("retroapi_test", "item_" + i));
		}

		LOGGER.info("Registered test_block, color_block, pipe, crate, freezer, test_item, + " + BLOCK_COUNT + " numbered blocks, + " + ITEM_COUNT + " numbered items");
	}
}
