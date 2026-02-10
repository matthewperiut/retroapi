package net.modificationstation.stationapi.mixin.render;

import net.minecraft.unmapped.C_71827890;
import net.minecraft.unmapped.C_81592558;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.CustomAtlasProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(C_71827890.class)
class BlockItemMixin implements CustomAtlasProvider {
    @Shadow private int blockId;

    @Override
    @Unique
    public Atlas getAtlas() {
        return C_81592558.f_44428008[blockId].getAtlas();
    }
}
