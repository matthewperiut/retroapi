package net.modificationstation.stationapi.api.block;

import net.minecraft.unmapped.C_64041439;
import net.minecraft.unmapped.C_81592558;
import net.modificationstation.stationapi.api.registry.RemappableRawIdHolder;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Util;

public interface StationBlock extends RemappableRawIdHolder {
    default C_81592558 setTranslationKey(Namespace namespace, String translationKey) {
        return Util.assertImpl();
    }

    default C_81592558 setTranslationKey(Identifier translationKey) {
        return setTranslationKey(translationKey.namespace, translationKey.path);
    }
    
    default boolean onBonemealUse(C_64041439 world, int x, int y, int z, BlockState state) {
        return false;
    }
}
