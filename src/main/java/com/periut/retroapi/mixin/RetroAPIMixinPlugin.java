package com.periut.retroapi.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RetroAPIMixinPlugin implements IMixinConfigPlugin {
	private static final Set<String> ATLAS_MIXINS = Set.of(
		"com.periut.retroapi.mixin.AchievementsScreenMixin",
		"com.periut.retroapi.mixin.BlockRendererAtlasMixin",
		"com.periut.retroapi.mixin.BlockParticleMixin",
		"com.periut.retroapi.mixin.ItemInHandRendererMixin",
		"com.periut.retroapi.mixin.ItemRendererMixin",
		"com.periut.retroapi.mixin.TextureManagerMixin"
	);

	private static final Set<String> STATIONAPI_DISABLED_MIXINS = Set.of(
		"com.periut.retroapi.mixin.ItemStackMixin",
		"com.periut.retroapi.mixin.BlockUpdatePacketMixin",
		"com.periut.retroapi.mixin.BlocksUpdatePacketMixin",
		"com.periut.retroapi.mixin.WorldChunkPacketMixin",
		"com.periut.retroapi.mixin.ChunkSendMixin",
		"com.periut.retroapi.mixin.ClientNetworkHandlerMixin"
	);

	private boolean stationAPIPresent;

	@Override
	public void onLoad(String mixinPackage) {
		stationAPIPresent = FabricLoader.getInstance().isModLoaded("stationapi");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (ATLAS_MIXINS.contains(mixinClassName) || STATIONAPI_DISABLED_MIXINS.contains(mixinClassName)) {
			return !stationAPIPresent;
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
