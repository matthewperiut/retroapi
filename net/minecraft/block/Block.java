package net.minecraft.block;

import java.util.ArrayList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeavesItem;
import net.minecraft.item.LogItem;
import net.minecraft.item.PistonBlockItem;
import net.minecraft.item.SaplingItem;
import net.minecraft.item.SlabItem;
import net.minecraft.item.WoolBlockItem;
import net.minecraft.locale.I18n;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.modificationstation.stationapi.api.block.StationBlock;
import net.modificationstation.stationapi.api.block.StationBlockItemsBlock;
import net.modificationstation.stationapi.api.block.StationFlatteningBlock;
import net.modificationstation.stationapi.api.block.StationItemsBlock;
import net.modificationstation.stationapi.api.client.block.StationRendererBlock;

public class Block implements StationBlockItemsBlock, StationBlock, StationFlatteningBlock, StationItemsBlock, StationRendererBlock {
	public static final Block.Sounds DEFAULT_SOUNDS = new Block.Sounds("stone", 1.0F, 1.0F);
	public static final Block.Sounds WOOD_SOUNDS = new Block.Sounds("wood", 1.0F, 1.0F);
	public static final Block.Sounds GRAVEL_SOUNDS = new Block.Sounds("gravel", 1.0F, 1.0F);
	public static final Block.Sounds GRASS_SOUNDS = new Block.Sounds("grass", 1.0F, 1.0F);
	public static final Block.Sounds STONE_SOUNDS = new Block.Sounds("stone", 1.0F, 1.0F);
	public static final Block.Sounds METAL_SOUNDS = new Block.Sounds("stone", 1.0F, 1.5F);
	public static final Block.Sounds GLASS_SOUNDS = new Block__96978149("stone", 1.0F, 1.0F);
	public static final Block.Sounds CLOTH_SOUNDS = new Block.Sounds("cloth", 1.0F, 1.0F);
	public static final Block.Sounds SAND_SOUNDS = new Block__48198598("sand", 1.0F, 1.0F);
	public static final Block[] BY_ID = new Block[256];
	public static final boolean[] TICKS_RANDOMLY = new boolean[256];
	public static final boolean[] IS_SOLID_RENDER = new boolean[256];
	public static final boolean[] HAS_BLOCK_ENTITY = new boolean[256];
	public static final int[] OPACITIES = new int[256];
	public static final boolean[] IS_TRANSLUCENT = new boolean[256];
	public static final int[] LIGHT = new int[256];
	public static final boolean[] UPDATE_CLIENTS = new boolean[256];
	public static final Block STONE = new StoneBlock(1, 1).setStrength(1.5F).setBlastResistance(10.0F).setSounds(STONE_SOUNDS).setKey("stone");
	public static final GrassBlock GRASS = (GrassBlock)new GrassBlock(2).setStrength(0.6F).setSounds(GRASS_SOUNDS).setKey("grass");
	public static final Block DIRT = new DirtBlock(3, 2).setStrength(0.5F).setSounds(GRAVEL_SOUNDS).setKey("dirt");
	public static final Block COBBLESTONE = new Block(4, 16, Material.STONE)
		.setStrength(2.0F)
		.setBlastResistance(10.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("stonebrick");
	public static final Block PLANKS = new Block(5, 4, Material.WOOD)
		.setStrength(2.0F)
		.setBlastResistance(5.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("wood")
		.updateClients();
	public static final Block SAPLING = new SaplingBlock(6, 15).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("sapling").updateClients();
	public static final Block BEDROCK = new Block(7, 17, Material.STONE)
		.setUnbreakable()
		.setBlastResistance(6000000.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("bedrock")
		.disableStats();
	public static final Block FLOWING_WATER = new FlowingLiquidBlock(8, Material.WATER)
		.setStrength(100.0F)
		.setOpacity(3)
		.setKey("water")
		.disableStats()
		.updateClients();
	public static final Block WATER = new LiquidSourceBlock(9, Material.WATER).setStrength(100.0F).setOpacity(3).setKey("water").disableStats().updateClients();
	public static final Block FLOWING_LAVA = new FlowingLiquidBlock(10, Material.LAVA)
		.setStrength(0.0F)
		.setLight(1.0F)
		.setOpacity(255)
		.setKey("lava")
		.disableStats()
		.updateClients();
	public static final Block LAVA = new LiquidSourceBlock(11, Material.LAVA)
		.setStrength(100.0F)
		.setLight(1.0F)
		.setOpacity(255)
		.setKey("lava")
		.disableStats()
		.updateClients();
	public static final Block SAND = new FallingBlock(12, 18).setStrength(0.5F).setSounds(SAND_SOUNDS).setKey("sand");
	public static final Block GRAVEL = new GravelBlock(13, 19).setStrength(0.6F).setSounds(GRAVEL_SOUNDS).setKey("gravel");
	public static final Block GOLD_ORE = new OreBlock(14, 32).setStrength(3.0F).setBlastResistance(5.0F).setSounds(STONE_SOUNDS).setKey("oreGold");
	public static final Block IRON_ORE = new OreBlock(15, 33).setStrength(3.0F).setBlastResistance(5.0F).setSounds(STONE_SOUNDS).setKey("oreIron");
	public static final Block COAL_ORE = new OreBlock(16, 34).setStrength(3.0F).setBlastResistance(5.0F).setSounds(STONE_SOUNDS).setKey("oreCoal");
	public static final Block LOG = new LogBlock(17).setStrength(2.0F).setSounds(WOOD_SOUNDS).setKey("log").updateClients();
	public static final LeavesBlock LEAVES = (LeavesBlock)new LeavesBlock(18, 52)
		.setStrength(0.2F)
		.setOpacity(1)
		.setSounds(GRASS_SOUNDS)
		.setKey("leaves")
		.disableStats()
		.updateClients();
	public static final Block SPONGE = new SpongeBlock(19).setStrength(0.6F).setSounds(GRASS_SOUNDS).setKey("sponge");
	public static final Block GLASS = new GlassBlock(20, 49, Material.GLASS, false).setStrength(0.3F).setSounds(GLASS_SOUNDS).setKey("glass");
	public static final Block LAPIS_ORE = new OreBlock(21, 160).setStrength(3.0F).setBlastResistance(5.0F).setSounds(STONE_SOUNDS).setKey("oreLapis");
	public static final Block LAPIS_BLOCK = new Block(22, 144, Material.STONE)
		.setStrength(3.0F)
		.setBlastResistance(5.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("blockLapis");
	public static final Block DISPENSER = new DispenserBlock(23).setStrength(3.5F).setSounds(STONE_SOUNDS).setKey("dispenser").updateClients();
	public static final Block SANDSTONE = new SandstoneBlock(24).setSounds(STONE_SOUNDS).setStrength(0.8F).setKey("sandStone");
	public static final Block NOTEBLOCK = new NoteBlock(25).setStrength(0.8F).setKey("musicBlock").updateClients();
	public static final Block BED = new BedBlock(26).setStrength(0.2F).setKey("bed").disableStats().updateClients();
	public static final Block POWERED_RAIL = new RailBlock(27, 179, true).setStrength(0.7F).setSounds(METAL_SOUNDS).setKey("goldenRail").updateClients();
	public static final Block DETECTOR_RAIL = new DetectorRailBlock(28, 195).setStrength(0.7F).setSounds(METAL_SOUNDS).setKey("detectorRail").updateClients();
	public static final Block STICKY_PISTON = new PistonBaseBlock(29, 106, true).setKey("pistonStickyBase").updateClients();
	public static final Block WEB = new CobwebBlock(30, 11).setOpacity(1).setStrength(4.0F).setKey("web");
	public static final TallPlantBlock TALLGRASS = (TallPlantBlock)new TallPlantBlock(31, 39).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("tallgrass");
	public static final DeadBushBlock DEADBUSH = (DeadBushBlock)new DeadBushBlock(32, 55).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("deadbush");
	public static final Block PISTON = new PistonBaseBlock(33, 107, false).setKey("pistonBase").updateClients();
	public static final PistonHeadBlock PISTON_HEAD = (PistonHeadBlock)new PistonHeadBlock(34, 107).updateClients();
	public static final Block WOOL = new WoolBlock().setStrength(0.8F).setSounds(CLOTH_SOUNDS).setKey("cloth").updateClients();
	public static final MovingBlock MOVING_BLOCK = new MovingBlock(36);
	public static final PlantBlock YELLOW_FLOWER = (PlantBlock)new PlantBlock(37, 13).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("flower");
	public static final PlantBlock RED_FLOWER = (PlantBlock)new PlantBlock(38, 12).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("rose");
	public static final PlantBlock BROWN_MUSHROOM = (PlantBlock)new MushroomPlantBlock(39, 29)
		.setStrength(0.0F)
		.setSounds(GRASS_SOUNDS)
		.setLight(0.125F)
		.setKey("mushroom");
	public static final PlantBlock RED_MUSHROOM = (PlantBlock)new MushroomPlantBlock(40, 28).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("mushroom");
	public static final Block GOLD_BLOCK = new MineralBlock(41, 23).setStrength(3.0F).setBlastResistance(10.0F).setSounds(METAL_SOUNDS).setKey("blockGold");
	public static final Block IRON_BLOCK = new MineralBlock(42, 22).setStrength(5.0F).setBlastResistance(10.0F).setSounds(METAL_SOUNDS).setKey("blockIron");
	public static final Block DOUBLE_STONE_SLAB = new StoneSlabBlock(43, true)
		.setStrength(2.0F)
		.setBlastResistance(10.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("stoneSlab");
	public static final Block STONE_SLAB = new StoneSlabBlock(44, false).setStrength(2.0F).setBlastResistance(10.0F).setSounds(STONE_SOUNDS).setKey("stoneSlab");
	public static final Block BRICKS = new Block(45, 7, Material.STONE).setStrength(2.0F).setBlastResistance(10.0F).setSounds(STONE_SOUNDS).setKey("brick");
	public static final Block TNT = new TntBlock(46, 8).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("tnt");
	public static final Block BOOKSHELF = new BookshelfBlock(47, 35).setStrength(1.5F).setSounds(WOOD_SOUNDS).setKey("bookshelf");
	public static final Block MOSSY_COBBLESTONE = new Block(48, 36, Material.STONE)
		.setStrength(2.0F)
		.setBlastResistance(10.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("stoneMoss");
	public static final Block OBSIDIAN = new ObsidianBlock(49, 37).setStrength(10.0F).setBlastResistance(2000.0F).setSounds(STONE_SOUNDS).setKey("obsidian");
	public static final Block TORCH = new TorchBlock(50, 80).setStrength(0.0F).setLight(0.9375F).setSounds(WOOD_SOUNDS).setKey("torch").updateClients();
	public static final FireBlock FIRE = (FireBlock)new FireBlock(51, 31)
		.setStrength(0.0F)
		.setLight(1.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("fire")
		.disableStats()
		.updateClients();
	public static final Block MOB_SPAWNER = new MobSpawnerBlock(52, 65).setStrength(5.0F).setSounds(METAL_SOUNDS).setKey("mobSpawner").disableStats();
	public static final Block OAK_STAIRS = new StairsBlock(53, PLANKS).setKey("stairsWood").updateClients();
	public static final Block CHEST = new ChestBlock(54).setStrength(2.5F).setSounds(WOOD_SOUNDS).setKey("chest").updateClients();
	public static final Block REDSTONE_WIRE = new RedstoneWireBlock(55, 164)
		.setStrength(0.0F)
		.setSounds(DEFAULT_SOUNDS)
		.setKey("redstoneDust")
		.disableStats()
		.updateClients();
	public static final Block DIAMOND_ORE = new OreBlock(56, 50).setStrength(3.0F).setBlastResistance(5.0F).setSounds(STONE_SOUNDS).setKey("oreDiamond");
	public static final Block DIAMOND_BLOCK = new MineralBlock(57, 24).setStrength(5.0F).setBlastResistance(10.0F).setSounds(METAL_SOUNDS).setKey("blockDiamond");
	public static final Block CRAFTING_TABLE = new CraftingTableBlock(58).setStrength(2.5F).setSounds(WOOD_SOUNDS).setKey("workbench");
	public static final Block WHEAT = new WheatBlock(59, 88).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("crops").disableStats().updateClients();
	public static final Block FARMLAND = new FarmlandBlock(60).setStrength(0.6F).setSounds(GRAVEL_SOUNDS).setKey("farmland");
	public static final Block FURNACE = new FurnaceBlock(61, false).setStrength(3.5F).setSounds(STONE_SOUNDS).setKey("furnace").updateClients();
	public static final Block LIT_FURNACE = new FurnaceBlock(62, true)
		.setStrength(3.5F)
		.setSounds(STONE_SOUNDS)
		.setLight(0.875F)
		.setKey("furnace")
		.updateClients();
	public static final Block STANDING_SIGN = new SignBlock(63, SignBlockEntity.class, true)
		.setStrength(1.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("sign")
		.disableStats()
		.updateClients();
	public static final Block WOODEN_DOOR = new DoorBlock(64, Material.WOOD)
		.setStrength(3.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("doorWood")
		.disableStats()
		.updateClients();
	public static final Block LADDER = new LadderBlock(65, 83).setStrength(0.4F).setSounds(WOOD_SOUNDS).setKey("ladder").updateClients();
	public static final Block RAIL = new RailBlock(66, 128, false).setStrength(0.7F).setSounds(METAL_SOUNDS).setKey("rail").updateClients();
	public static final Block STONE_STAIRS = new StairsBlock(67, COBBLESTONE).setKey("stairsStone").updateClients();
	public static final Block WALL_SIGN = new SignBlock(68, SignBlockEntity.class, false)
		.setStrength(1.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("sign")
		.disableStats()
		.updateClients();
	public static final Block LEVER = new LeverBlock(69, 96).setStrength(0.5F).setSounds(WOOD_SOUNDS).setKey("lever").updateClients();
	public static final Block STONE_PRESSURE_PLATE;
	public static final Block IRON_DOOR = new DoorBlock(71, Material.IRON)
		.setStrength(5.0F)
		.setSounds(METAL_SOUNDS)
		.setKey("doorIron")
		.disableStats()
		.updateClients();
	public static final Block WOODEN_PRESSURE_PLATE;
	public static final Block REDSTONE_ORE = new RedstoneOreBlock(73, 51, false)
		.setStrength(3.0F)
		.setBlastResistance(5.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("oreRedstone")
		.updateClients();
	public static final Block LIT_REDSTONE_ORE = new RedstoneOreBlock(74, 51, true)
		.setLight(0.625F)
		.setStrength(3.0F)
		.setBlastResistance(5.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("oreRedstone")
		.updateClients();
	public static final Block UNLIT_REDSTONE_TORCH = new RedstoneTorchBlock(75, 115, false)
		.setStrength(0.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("notGate")
		.updateClients();
	public static final Block REDSTONE_TORCH = new RedstoneTorchBlock(76, 99, true)
		.setStrength(0.0F)
		.setLight(0.5F)
		.setSounds(WOOD_SOUNDS)
		.setKey("notGate")
		.updateClients();
	public static final Block STONE_BUTTON;
	public static final Block SNOW_LAYER = new SnowLayerBlock(78, 66).setStrength(0.1F).setSounds(CLOTH_SOUNDS).setKey("snow");
	public static final Block ICE = new IceBlock(79, 67).setStrength(0.5F).setOpacity(3).setSounds(GLASS_SOUNDS).setKey("ice");
	public static final Block SNOW = new SnowBlock(80, 66).setStrength(0.2F).setSounds(CLOTH_SOUNDS).setKey("snow");
	public static final Block CACTUS = new CactusBlock(81, 70).setStrength(0.4F).setSounds(CLOTH_SOUNDS).setKey("cactus");
	public static final Block CLAY = new ClayBlock(82, 72).setStrength(0.6F).setSounds(GRAVEL_SOUNDS).setKey("clay");
	public static final Block REEDS = new SugarCaneBlock(83, 73).setStrength(0.0F).setSounds(GRASS_SOUNDS).setKey("reeds").disableStats();
	public static final Block JUKEBOX = new JukeboxBlock(84, 74)
		.setStrength(2.0F)
		.setBlastResistance(10.0F)
		.setSounds(STONE_SOUNDS)
		.setKey("jukebox")
		.updateClients();
	public static final Block FENCE = new FenceBlock(85, 4).setStrength(2.0F).setBlastResistance(5.0F).setSounds(WOOD_SOUNDS).setKey("fence").updateClients();
	public static final Block PUMPKIN = new PumpkinBlock(86, 102, false).setStrength(1.0F).setSounds(WOOD_SOUNDS).setKey("pumpkin").updateClients();
	public static final Block NETHERRACK = new NetherrackBlock(87, 103).setStrength(0.4F).setSounds(STONE_SOUNDS).setKey("hellrock");
	public static final Block SOUL_SAND = new SoulSandBlock(88, 104).setStrength(0.5F).setSounds(SAND_SOUNDS).setKey("hellsand");
	public static final Block GLOWSTONE = new GlowstoneBlock(89, 105, Material.STONE).setStrength(0.3F).setSounds(GLASS_SOUNDS).setLight(1.0F).setKey("lightgem");
	public static final PortalBlock NETHER_PORTAL = (PortalBlock)new PortalBlock(90, 14)
		.setStrength(-1.0F)
		.setSounds(GLASS_SOUNDS)
		.setLight(0.75F)
		.setKey("portal");
	public static final Block LIT_PUMPKIN = new PumpkinBlock(91, 102, true)
		.setStrength(1.0F)
		.setSounds(WOOD_SOUNDS)
		.setLight(1.0F)
		.setKey("litpumpkin")
		.updateClients();
	public static final Block CAKE = new CakeBlock(92, 121).setStrength(0.5F).setSounds(CLOTH_SOUNDS).setKey("cake").disableStats().updateClients();
	public static final Block REPEATER = new RepeaterBlock(93, false).setStrength(0.0F).setSounds(WOOD_SOUNDS).setKey("diode").disableStats().updateClients();
	public static final Block POWERED_REPEATER = new RepeaterBlock(94, true)
		.setStrength(0.0F)
		.setLight(0.625F)
		.setSounds(WOOD_SOUNDS)
		.setKey("diode")
		.disableStats()
		.updateClients();
	public static final Block CHEST_LOCKED_APRIL_FOOLS = new AprilFoolsChestBlock(95)
		.setStrength(0.0F)
		.setLight(1.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("lockedchest")
		.setTicksRandomly(true)
		.updateClients();
	public static final Block TRAPDOOR = new TrapdoorBlock(96, Material.WOOD)
		.setStrength(3.0F)
		.setSounds(WOOD_SOUNDS)
		.setKey("trapdoor")
		.disableStats()
		.updateClients();
	public int sprite;
	public final int id;
	protected float miningTime;
	protected float blastResistance;
	protected boolean f_12284928 = true;
	protected boolean stats = true;
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	public Block.Sounds sounds = DEFAULT_SOUNDS;
	public float gravity = 1.0F;
	public final Material material;
	public float slipperiness = 0.6F;
	private String key;

	public Block(int id, Material material) {
		if (BY_ID[id] != null) {
			throw new IllegalArgumentException("Slot " + id + " is already occupied by " + BY_ID[id] + " when adding " + this);
		} else {
			this.material = material;
			BY_ID[id] = this;
			this.id = id;
			this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			IS_SOLID_RENDER[id] = this.isSolidRender();
			OPACITIES[id] = this.isSolidRender() ? 255 : 0;
			IS_TRANSLUCENT[id] = !material.isOpaque();
			HAS_BLOCK_ENTITY[id] = false;
		}
	}

	public Block updateClients() {
		UPDATE_CLIENTS[this.id] = true;
		return this;
	}

	protected void init() {
	}

	public Block(int id, int sprite, Material material) {
		this(id, material);
		this.sprite = sprite;
	}

	/**
	 * Sets this block's sound set.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setSounds(Block.Sounds sounds) {
		this.sounds = sounds;
		return this;
	}

	/**
	 * Sets this block's light opacity.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setOpacity(int opacity) {
		OPACITIES[this.id] = opacity;
		return this;
	}

	/**
	 * Sets this block's light level.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setLight(float light) {
		LIGHT[this.id] = (int)(15.0F * light);
		return this;
	}

	/**
	 * Sets this block's resistance.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setBlastResistance(float blastResistance) {
		this.blastResistance = blastResistance * 3.0F;
		return this;
	}

	public boolean isCube() {
		return true;
	}

	/**
	 * Returns this block's render type. The possible values are as follows:
	 * <br>-1: none
	 * <br>1: liquid
	 * <br>2: animated block entity
	 * <br>3: block model
	 */
	@Environment(EnvType.CLIENT)
	public int getRenderType() {
		return 0;
	}

	/**
	 * Sets this block's mining speed and updates its blast resistance if needed.
	 * A block's blast resistance cannot be less than 5 times its mining speed.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setStrength(float strength) {
		this.miningTime = strength;
		if (this.blastResistance < strength * 5.0F) {
			this.blastResistance = strength * 5.0F;
		}

		return this;
	}

	/**
	 * Sets this block as unbreakable by setting its strength to -1. Note that this changes both
	 * the block's mining speed and its blast resistance.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setUnbreakable() {
		this.setStrength(-1.0F);
		return this;
	}

	public float getMiningTime() {
		return this.miningTime;
	}

	/**
	 * Sets whether this block accepts random ticks. Random ticks are mostly used by plants
	 * and crops to update growth, but some other blocks are affected by random ticks as well,
	 * like e.g. leaves (to update decay).
	 * Note that while this field might be set to true the block might not do anything with the random tick.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setTicksRandomly(boolean ticksRandomly) {
		TICKS_RANDOMLY[this.id] = (boolean)ticksRandomly;
		return this;
	}

	public void setShape(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	@Environment(EnvType.CLIENT)
	public float getBrightness(WorldView world, int x, int y, int z) {
		return world.getBrightness(x, y, z, LIGHT[this.id]);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderFace(WorldView world, int x, int y, int z, int face) {
		if (face == 0 && this.minY > 0.0) {
			return true;
		} else if (face == 1 && this.maxY < 1.0) {
			return true;
		} else if (face == 2 && this.minZ > 0.0) {
			return true;
		} else if (face == 3 && this.maxZ < 1.0) {
			return true;
		} else if (face == 4 && this.minX > 0.0) {
			return true;
		} else {
			return face == 5 && this.maxX < 1.0 ? true : !world.isSolidRender(x, y, z);
		}
	}

	public boolean isFaceSolid(WorldView world, int x, int y, int z, int face) {
		return world.getMaterial(x, y, z).isSolid();
	}

	@Environment(EnvType.CLIENT)
	public int getSprite(WorldView world, int x, int y, int z, int face) {
		return this.getSprite(face, world.getBlockMetadata(x, y, z));
	}

	public int getSprite(int face, int metadata) {
		return this.getSprite(face);
	}

	public int getSprite(int face) {
		return this.sprite;
	}

	@Environment(EnvType.CLIENT)
	public Box getOutlineShape(World world, int x, int y, int z) {
		return Box.fromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	public void addCollisions(World world, int x, int y, int z, Box shape, ArrayList collisions) {
		Box box = this.getCollisionShape(world, x, y, z);
		if (box != null && shape.intersects(box)) {
			collisions.add(box);
		}
	}

	public Box getCollisionShape(World world, int x, int y, int z) {
		return Box.fromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	public boolean isSolidRender() {
		return true;
	}

	public boolean canRayTrace(int metadata, boolean allowLiquids) {
		return this.canRayTrace();
	}

	public boolean canRayTrace() {
		return true;
	}

	/**
	 * Performs a scheduled tick. Blocks that want to perform some action after a delay can
	 * tell the world to schedule the update, which is then executed by calling this method.
	 */
	public void tick(World world, int x, int y, int z, Random random) {
	}

	/**
	 * Performs a random display tick. Random display ticks are mostly used to spawn particles
	 * around a block. The client does 1000 random display tick attempts within a radius of
	 * 16 blocks of the player each tick.
	 */
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
	}

	public void onBroken(World world, int x, int y, int z, int metadata) {
	}

	/**
	 * Performs a block update. This method is called when a neighboring block has updated
	 * (i.e. placed, broken, or otherwise changed state).
	 */
	public void neighborChanged(World world, int x, int y, int z, int neighborBlock) {
	}

	public int getTickRate() {
		return 10;
	}

	public void onAdded(World world, int x, int y, int z) {
	}

	public void onRemoved(World world, int x, int y, int z) {
	}

	public int getBaseDropCount(Random random) {
		return 1;
	}

	public int getDropItem(int metadata, Random random) {
		return this.id;
	}

	public float getMiningSpeed(PlayerEntity player) {
		if (this.miningTime < 0.0F) {
			return 0.0F;
		} else {
			return !player.canMineBlock(this) ? 1.0F / this.miningTime / 100.0F : player.getMiningSpeed(this) / this.miningTime / 30.0F;
		}
	}

	public final void dropItems(World world, int x, int y, int z, int metadata) {
		this.dropItems(world, x, y, z, metadata, 1.0F);
	}

	public void dropItems(World world, int x, int y, int z, int metadata, float luck) {
		if (!world.isMultiplayer) {
			int i = this.getBaseDropCount(world.random);

			for (int j = 0; j < i; j++) {
				if (!(world.random.nextFloat() > luck)) {
					int k = this.getDropItem(metadata, world.random);
					if (k > 0) {
						this.dropItem(world, x, y, z, new ItemStack(k, 1, this.getDropItemMetadata(metadata)));
					}
				}
			}
		}
	}

	protected void dropItem(World world, int x, int y, int z, ItemStack item) {
		if (!world.isMultiplayer) {
			float f6 = 0.7F;
			double d = world.random.nextFloat() * f6 + (1.0F - f6) * 0.5;
			double e = world.random.nextFloat() * f6 + (1.0F - f6) * 0.5;
			double f9 = world.random.nextFloat() * f6 + (1.0F - f6) * 0.5;
			ItemEntity itemEntity = new ItemEntity(world, x + d, y + e, z + f9, item);
			itemEntity.pickUpDelay = 10;
			world.addEntity(itemEntity);
		}
	}

	protected int getDropItemMetadata(int metadata) {
		return 0;
	}

	/**
	 * Returns this block's blast resistance for the given entity.
	 */
	public float getBlastResistance(Entity entity) {
		return this.blastResistance / 5.0F;
	}

	public HitResult rayTrace(World world, int x, int y, int z, Vec3d from, Vec3d to) {
		this.updateShape(world, x, y, z);
		from = from.add(-x, -y, -z);
		to = to.add(-x, -y, -z);
		Vec3d vec3d7 = from.intermediateWithX(to, this.minX);
		Vec3d vec3d8 = from.intermediateWithX(to, this.maxX);
		Vec3d vec3d9 = from.intermediateWithY(to, this.minY);
		Vec3d vec3d10 = from.intermediateWithY(to, this.maxY);
		Vec3d vec3d11 = from.intermediateWithZ(to, this.minZ);
		Vec3d vec3d12 = from.intermediateWithZ(to, this.maxZ);
		if (!this.containsX(vec3d7)) {
			vec3d7 = null;
		}

		if (!this.containsX(vec3d8)) {
			vec3d8 = null;
		}

		if (!this.containsY(vec3d9)) {
			vec3d9 = null;
		}

		if (!this.containsY(vec3d10)) {
			vec3d10 = null;
		}

		if (!this.containsZ(vec3d11)) {
			vec3d11 = null;
		}

		if (!this.containsZ(vec3d12)) {
			vec3d12 = null;
		}

		Object object = null;
		if (vec3d7 != null && (object == null || from.distanceTo(vec3d7) < from.distanceTo((Vec3d)object))) {
			object = vec3d7;
		}

		if (vec3d8 != null && (object == null || from.distanceTo(vec3d8) < from.distanceTo((Vec3d)object))) {
			object = vec3d8;
		}

		if (vec3d9 != null && (object == null || from.distanceTo(vec3d9) < from.distanceTo((Vec3d)object))) {
			object = vec3d9;
		}

		if (vec3d10 != null && (object == null || from.distanceTo(vec3d10) < from.distanceTo((Vec3d)object))) {
			object = vec3d10;
		}

		if (vec3d11 != null && (object == null || from.distanceTo(vec3d11) < from.distanceTo((Vec3d)object))) {
			object = vec3d11;
		}

		if (vec3d12 != null && (object == null || from.distanceTo(vec3d12) < from.distanceTo((Vec3d)object))) {
			object = vec3d12;
		}

		if (object == null) {
			return null;
		} else {
			int i = -1;
			if (object == vec3d7) {
				i = 4;
			}

			if (object == vec3d8) {
				i = 5;
			}

			if (object == vec3d9) {
				i = 0;
			}

			if (object == vec3d10) {
				i = 1;
			}

			if (object == vec3d11) {
				i = 2;
			}

			if (object == vec3d12) {
				i = 3;
			}

			return new HitResult(x, y, z, i, ((Vec3d)object).add(x, y, z));
		}
	}

	private boolean containsX(Vec3d pos) {
		return pos == null ? false : pos.y >= this.minY && pos.y <= this.maxY && pos.z >= this.minZ && pos.z <= this.maxZ;
	}

	private boolean containsY(Vec3d pos) {
		return pos == null ? false : pos.x >= this.minX && pos.x <= this.maxX && pos.z >= this.minZ && pos.z <= this.maxZ;
	}

	private boolean containsZ(Vec3d pos) {
		return pos == null ? false : pos.x >= this.minX && pos.x <= this.maxX && pos.y >= this.minY && pos.y <= this.maxY;
	}

	public void onExploded(World world, int x, int y, int z) {
	}

	/**
	 * Returns this block's render layer. The possible values are as follows:
	 * <br>0: solid
	 * <br>1: mipped cutout
	 * <br>2: cutout
	 * <br>3: translucent
	 */
	@Environment(EnvType.CLIENT)
	public int getRenderLayer() {
		return 0;
	}

	public boolean canBePlaced(World world, int x, int y, int z, int face) {
		return this.canBePlaced(world, x, y, z);
	}

	public boolean canBePlaced(World world, int x, int y, int z) {
		int i = world.getBlock(x, y, z);
		return i == 0 || BY_ID[i].material.isReplaceable();
	}

	public boolean use(World world, int x, int y, int z, PlayerEntity player) {
		return false;
	}

	public void onSteppedOn(World world, int x, int y, int z, Entity entity) {
	}

	public void updateMetadataOnPlaced(World world, int x, int y, int z, int face) {
	}

	public void startMining(World world, int x, int y, int z, PlayerEntity player) {
	}

	public void applyMaterialDrag(World world, int x, int y, int z, Entity entity, Vec3d velocity) {
	}

	public void updateShape(WorldView world, int x, int y, int z) {
	}

	@Environment(EnvType.CLIENT)
	public int getColor(int metadata) {
		return 16777215;
	}

	@Environment(EnvType.CLIENT)
	public int getColor(WorldView world, int x, int y, int z) {
		return 16777215;
	}

	/**
	 * Returns whether this block is emitting a redstone signal in the given direction.
	 * This roughly equates to what is colloquially known as 'soft' or 'weak' power.
	 *
	 * <p>
	 * NOTE: directions in redstone signal related methods are backwards, so blocks should
	 * check for the signal emitted in the direction <i>opposite</i> of the one given.
	 */
	public boolean hasSignal(WorldView world, int x, int y, int z, int dir) {
		return false;
	}

	/**
	 * Returns whether this block is capable of emitting a redstone signal.
	 */
	public boolean isSignalSource() {
		return false;
	}

	public void onEntityCollision(World world, int x, int y, int z, Entity entity) {
	}

	public boolean hasDirectSignal(World world, int x, int y, int z, int dir) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public void resetShape() {
	}

	public void afterMinedByPlayer(World world, PlayerEntity player, int x, int y, int z, int metadata) {
		player.incrementStat(Stats.BLOCKS_MINED[this.id], 1);
		this.dropItems(world, x, y, z, metadata);
	}

	public boolean canSurvive(World world, int x, int y, int z) {
		return true;
	}

	/**
	 * Called when this block is placed by an entity.
	 */
	public void onPlaced(World world, int x, int y, int z, MobEntity entity) {
	}

	/**
	 * Sets this block's translation and registry key.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block setKey(String key) {
		this.key = "tile." + key;
		return this;
	}

	public String getName() {
		return I18n.translate(this.getTranslationKey() + ".name");
	}

	public String getTranslationKey() {
		return this.key;
	}

	/**
	 * Performs a block event. Block events are queued on the server, and executed once per
	 * tick. Successful block events are synced with clients that are within range. Block events
	 * are most notably used by pistons to handle extension and retraction, but note blocks
	 * also use them to play sounds.
	 */
	public void doEvent(World world, int x, int y, int z, int type, int data) {
	}

	public boolean hasStats() {
		return this.stats;
	}

	/**
	 * Disables tracking by stats for this block.
	 *
	 * <p>
	 * NOTE: this method should only be used during the block's creation before it is registered.
	 */
	public Block disableStats() {
		this.stats = false;
		return this;
	}

	/**
	 * Returns how this block interacts with pistons. The following values are accepted:
	 * <br>- 0: this block can be pushed and pulled by pistons.
	 * <br>- 1: this block is broken when pushed.
	 * <br>- 2: this block cannot be pushed or pulled by pistons.
	 */
	public int getPistonMoveBehavior() {
		return this.material.getPistonMoveBehavior();
	}

	static {
		STONE_PRESSURE_PLATE = new PressurePlateBlock(70, STONE.sprite, PressurePlateBlock__ActivationRule.MOBS, Material.STONE)
			.setStrength(0.5F)
			.setSounds(STONE_SOUNDS)
			.setKey("pressurePlate")
			.updateClients();
		WOODEN_PRESSURE_PLATE = new PressurePlateBlock(72, PLANKS.sprite, PressurePlateBlock__ActivationRule.EVERYTHING, Material.WOOD)
			.setStrength(0.5F)
			.setSounds(WOOD_SOUNDS)
			.setKey("pressurePlate")
			.updateClients();
		STONE_BUTTON = new ButtonBlock(77, STONE.sprite).setStrength(0.5F).setSounds(STONE_SOUNDS).setKey("button").updateClients();
		Item.BY_ID[WOOL.id] = new WoolBlockItem(WOOL.id - 256).setKey("cloth");
		Item.BY_ID[LOG.id] = new LogItem(LOG.id - 256).setKey("log");
		Item.BY_ID[STONE_SLAB.id] = new SlabItem(STONE_SLAB.id - 256).setKey("stoneSlab");
		Item.BY_ID[SAPLING.id] = new SaplingItem(SAPLING.id - 256).setKey("sapling");
		Item.BY_ID[LEAVES.id] = new LeavesItem(LEAVES.id - 256).setKey("leaves");
		Item.BY_ID[PISTON.id] = new PistonBlockItem(PISTON.id - 256);
		Item.BY_ID[STICKY_PISTON.id] = new PistonBlockItem(STICKY_PISTON.id - 256);

		for (int i = 0; i < 256; i++) {
			if (BY_ID[i] != null && Item.BY_ID[i] == null) {
				Item.BY_ID[i] = new BlockItem(i - 256);
				BY_ID[i].init();
			}
		}

		IS_TRANSLUCENT[0] = true;
		Stats.initBlockStats();
	}
}
