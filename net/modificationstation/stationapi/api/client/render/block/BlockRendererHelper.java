package net.modificationstation.stationapi.api.client.render.block;

import net.minecraft.unmapped.C_03670941;

public interface BlockRendererHelper {

    static BlockRendererHelper of(C_03670941 blockRenderer) {
        return () -> blockRenderer;
    }

    C_03670941 blockRenderer();

    default void setBottomFaceRotation(int rotation) {
        BlockRendererUtil.setBottomFaceRotation(blockRenderer(), rotation);
    }

    default void setTopFaceRotation(int rotation) {
        BlockRendererUtil.setTopFaceRotation(blockRenderer(), rotation);
    }

    default void setEastFaceRotation(int rotation) {
        BlockRendererUtil.setEastFaceRotation(blockRenderer(), rotation);
    }

    default void setWestFaceRotation(int rotation) {
        BlockRendererUtil.setWestFaceRotation(blockRenderer(), rotation);
    }

    default void setNorthFaceRotation(int rotation) {
        BlockRendererUtil.setNorthFaceRotation(blockRenderer(), rotation);
    }

    default void setSouthFaceRotation(int rotation) {
        BlockRendererUtil.setSouthFaceRotation(blockRenderer(), rotation);
    }
}
