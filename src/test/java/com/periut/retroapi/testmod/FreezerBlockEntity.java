package com.periut.retroapi.testmod;

import net.minecraft.block.Block;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FreezerBlockEntity extends FurnaceBlockEntity {

	@Override
	public String getInventoryName() {
		return "Freezer";
	}

	@Override
	public void tick() {
		boolean changed = false;

		if (fuelTime > 0) {
			fuelTime--;
		}

		if (!world.isMultiplayer) {
			if (fuelTime == 0 && canFreeze()) {
				totalFuelTime = fuelTime = getFreezerFuelTime(getItem(1));
				if (fuelTime > 0) {
					changed = true;
					ItemStack fuel = getItem(1);
					if (fuel != null) {
						fuel.size--;
						if (fuel.size == 0) {
							setItem(1, null);
						}
					}
				}
			}

			if (fuelTime > 0 && canFreeze()) {
				cookTime++;
				if (cookTime >= 200) {
					cookTime = 0;
					finishFreezing();
					changed = true;
				}
			} else {
				cookTime = 0;
			}
		}

		if (changed) {
			markDirty();
		}
	}

	private boolean canFreeze() {
		ItemStack input = getItem(0);
		if (input == null) return false;
		if (input.getItem().id != Item.WATER_BUCKET.id) return false;
		ItemStack output = getItem(2);
		if (output == null) return true;
		if (output.getItem().id != Block.ICE.id) return false;
		return output.size < getMaxStackSize() && output.size < output.getMaxSize();
	}

	private void finishFreezing() {
		if (!canFreeze()) return;

		ItemStack output = getItem(2);
		if (output == null) {
			setItem(2, new ItemStack(Block.ICE, 1));
		} else {
			output.size++;
		}

		ItemStack input = getItem(0);
		input.size--;
		if (input.size <= 0) {
			setItem(0, new ItemStack(Item.BUCKET, 1));
		}
	}

	private int getFreezerFuelTime(ItemStack stack) {
		if (stack == null) return 0;
		int id = stack.getItem().id;
		if (id < 256 && Block.BY_ID[id] != null && Block.BY_ID[id].material == Material.WOOD) return 300;
		if (id == Item.STICK.id) return 100;
		if (id == Item.COAL.id) return 1600;
		if (id == Item.LAVA_BUCKET.id) return 20000;
		if (id == Block.SAPLING.id) return 100;
		return 0;
	}
}
