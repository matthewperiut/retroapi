package net.modificationstation.stationapi.mixin.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.unmapped.C_98888256;
import net.modificationstation.stationapi.api.client.item.StationRendererItem;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.client.texture.binder.StationTextureBinder;
import net.modificationstation.stationapi.api.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

@Mixin(C_98888256.class)
@EnvironmentInterface(value = EnvType.CLIENT, itf = StationRendererItem.class)
abstract class ItemMixin implements StationRendererItem {
    @Shadow public abstract C_98888256 setTextureId(int i);

    @Shadow public abstract C_98888256 setTexturePosition(int i, int j);

    @Override
    @Environment(EnvType.CLIENT)
    @Unique
    public Atlas getAtlas() {
        return Atlases.getGuiItems();
    }

    @Override
    @Environment(EnvType.CLIENT)
    @Unique
    public Atlas.Sprite setTexture(Identifier textureIdentifier) {
        Atlas.Sprite texture = ((ExpandableAtlas) getAtlas()).addTexture(textureIdentifier);
        setTextureId(texture.index);
        return texture;
    }

    @Override
    @Deprecated
    @Environment(EnvType.CLIENT)
    @Unique
    public Atlas.Sprite setTexture(String texturePath) {
        Atlas.Sprite texture = ((ExpandableAtlas) getAtlas()).addTexture(texturePath);
        setTextureId(texture.index);
        return texture;
    }

    @Override
    @Environment(EnvType.CLIENT)
    @Unique
    public <E extends StationTextureBinder> E setTextureBinder(Identifier staticReference, Function<Atlas.Sprite, E> initializer) {
        E textureBinder = ((ExpandableAtlas) getAtlas()).addTextureBinder(staticReference, initializer);
        setTextureId(textureBinder.f_82653629);
        return textureBinder;
    }
}
