package net.modificationstation.stationapi.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.unmapped.C_81592558;
import net.modificationstation.stationapi.api.block.StationBlock;
import net.modificationstation.stationapi.api.util.Namespace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(C_81592558.class)
abstract class BlockMixin implements StationBlock {
    @Shadow public abstract C_81592558 setTranslationKey(String string);

    @Override
    public C_81592558 setTranslationKey(Namespace namespace, String translationKey) {
        return setTranslationKey(namespace + "." + translationKey);
    }

    @WrapOperation(
            method = "getDroppedItemId",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/block/Block;id:I"
            )
    )
    private int stationapi_returnCorrectItem(C_81592558 instance, Operation<Integer> original) {
        return instance.asItem().f_57562535;
    }
}
