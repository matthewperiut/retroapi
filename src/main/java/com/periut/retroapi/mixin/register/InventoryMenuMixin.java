package com.periut.retroapi.mixin.register;

import com.periut.retroapi.register.blockentity.SyncField;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.InventoryMenuListener;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
	@Shadow
	protected List<InventoryMenuListener> listeners;

	@Shadow
	public List<InventorySlot> slots;

	@Unique
	private Object[] retroapi$syncTargets;

	@Unique
	private SyncEntry[] retroapi$syncEntries;

	@Unique
	private int[] retroapi$syncCache;

	@Unique
	private boolean retroapi$syncDiscovered;

	@Unique
	private void retroapi$discoverSyncFields() {
		if (retroapi$syncDiscovered) return;
		retroapi$syncDiscovered = true;

		List<SyncEntry> entries = new ArrayList<>();
		Class<?> menuClass = this.getClass();

		// Collect unique inventories from slots (excluding player inventory)
		Set<Object> inventories = new LinkedHashSet<>();
		for (InventorySlot slot : slots) {
			Inventory inv = ((InventorySlotAccessor) slot).getInventory();
			if (inv != null && !(inv instanceof net.minecraft.entity.mob.player.PlayerInventory)) {
				inventories.add(inv);
			}
		}
		retroapi$syncTargets = inventories.toArray();

		for (Field menuField : menuClass.getDeclaredFields()) {
			SyncField annotation = menuField.getAnnotation(SyncField.class);
			if (annotation == null) continue;
			if (menuField.getType() != int.class) {
				throw new IllegalStateException("@SyncField can only be applied to int fields, but " +
					menuField.getName() + " is " + menuField.getType().getName());
			}

			String targetFieldName = annotation.value();
			Field targetField = null;
			Object targetObject = null;

			// Search through slot inventories for the target field (walk class hierarchy)
			for (Object inv : retroapi$syncTargets) {
				for (Class<?> cls = inv.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass()) {
					try {
						targetField = cls.getDeclaredField(targetFieldName);
						targetObject = inv;
						break;
					} catch (NoSuchFieldException ignored) {
					}
				}
				if (targetField != null) break;
			}

			if (targetField == null || targetObject == null) {
				throw new IllegalStateException("@SyncField(\"" + targetFieldName + "\") on " +
					menuField.getName() + ": no such field found on any slot inventory");
			}
			if (targetField.getType() != int.class) {
				throw new IllegalStateException("@SyncField(\"" + targetFieldName + "\"): field must be int, but is " +
					targetField.getType().getName());
			}

			menuField.setAccessible(true);
			targetField.setAccessible(true);
			entries.add(new SyncEntry(menuField, targetField, targetObject));
		}

		retroapi$syncEntries = entries.toArray(new SyncEntry[0]);
		retroapi$syncCache = new int[retroapi$syncEntries.length];
	}

	@Inject(method = "addListener", at = @At("TAIL"), require = 0)
	private void retroapi$onAddListener(InventoryMenuListener listener, CallbackInfo ci) {
		retroapi$discoverSyncFields();
		if (retroapi$syncEntries.length == 0) return;

		for (int i = 0; i < retroapi$syncEntries.length; i++) {
			int value = retroapi$syncEntries[i].getTargetValue();
			listener.onDataChanged((InventoryMenu) (Object) this, i, value);
			retroapi$syncCache[i] = value;
		}
	}

	@Inject(method = "updateListeners", at = @At("TAIL"))
	private void retroapi$onUpdateListeners(CallbackInfo ci) {
		retroapi$discoverSyncFields();
		if (retroapi$syncEntries.length == 0) return;

		for (int i = 0; i < retroapi$syncEntries.length; i++) {
			int value = retroapi$syncEntries[i].getTargetValue();
			if (value != retroapi$syncCache[i]) {
				for (InventoryMenuListener listener : listeners) {
					listener.onDataChanged((InventoryMenu) (Object) this, i, value);
				}
				retroapi$syncEntries[i].setMenuValue(this, value);
				retroapi$syncCache[i] = value;
			}
		}
	}

	@Inject(method = "setData(II)V", at = @At("TAIL"), require = 0)
	private void retroapi$onSetData(int id, int value, CallbackInfo ci) {
		retroapi$discoverSyncFields();
		if (retroapi$syncEntries == null) return;
		if (id >= 0 && id < retroapi$syncEntries.length) {
			retroapi$syncEntries[id].setTargetValue(value);
		}
	}

	@Unique
	private static class SyncEntry {
		private final Field menuField;
		private final Field targetField;
		private final Object targetObject;

		SyncEntry(Field menuField, Field targetField, Object targetObject) {
			this.menuField = menuField;
			this.targetField = targetField;
			this.targetObject = targetObject;
		}

		int getTargetValue() {
			try {
				return targetField.getInt(targetObject);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		void setTargetValue(int value) {
			try {
				targetField.setInt(targetObject, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		void setMenuValue(Object menu, int value) {
			try {
				menuField.setInt(menu, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
