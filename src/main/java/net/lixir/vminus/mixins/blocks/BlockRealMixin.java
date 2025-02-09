package net.lixir.vminus.mixins.blocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lixir.vminus.util.SoundHelper;
import net.lixir.vminus.core.Visions;
import net.lixir.vminus.core.VisionProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Block.class)
public abstract class BlockRealMixin {
    @Unique
    private final Block vminus$block = (Block) (Object) this;

    @Inject(method = "getSpeedFactor", at = @At("RETURN"), cancellable = true)
    private void getSpeedFactor(CallbackInfoReturnable<Float> cir) {
        if (VisionProperties.searchElement(VisionProperties.Names.SPEED_FACTOR, vminus$block) != null)
            cir.setReturnValue(VisionProperties.getNumber(VisionProperties.Names.SPEED_FACTOR, vminus$block, cir.getReturnValue()).floatValue());
    }

    @Inject(method = "getFriction", at = @At("RETURN"), cancellable = true)
    private void getFriction(CallbackInfoReturnable<Float> cir) {
        if (VisionProperties.searchElement(VisionProperties.Names.FRICTION, vminus$block) != null)
            cir.setReturnValue(VisionProperties.getNumber(VisionProperties.Names.FRICTION, vminus$block, cir.getReturnValue()).floatValue());
    }

    @Inject(method = "getJumpFactor", at = @At("RETURN"), cancellable = true)
    private void getJumpFactor(CallbackInfoReturnable<Float> cir) {
        if (VisionProperties.searchElement(VisionProperties.Names.JUMP_FACTOR, vminus$block) != null)
            cir.setReturnValue(VisionProperties.getNumber(VisionProperties.Names.JUMP_FACTOR, vminus$block, cir.getReturnValue()).floatValue());
    }

    @Inject(method = "getExplosionResistance", at = @At("RETURN"), cancellable = true)
    private void getExplosionResistance(CallbackInfoReturnable<Float> cir) {
        if (VisionProperties.searchElement(VisionProperties.Names.BLAST_RESISTANCE, vminus$block) != null)
            cir.setReturnValue(VisionProperties.getNumber(VisionProperties.Names.BLAST_RESISTANCE, vminus$block, cir.getReturnValue()).floatValue());
    }

    @Inject(method = "getSoundType", at = @At("RETURN"), cancellable = true)
    private void getSoundType(BlockState state, CallbackInfoReturnable<SoundType> cir) {
        JsonObject visionData = Visions.getData(vminus$block);
        if (visionData == null) return;

        JsonElement soundElement = VisionProperties.searchElement(VisionProperties.Names.SOUND, visionData, vminus$block);
        if (soundElement == null)
            return;
        JsonObject soundObject;

        if (soundElement.isJsonObject()) {
            soundObject = soundElement.getAsJsonObject();
        } else
            return;

        String stateId = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString();

        if (Visions.BLOCK_SOUND_TYPE_CACHE.containsKey(stateId)) {
            cir.setReturnValue(Visions.BLOCK_SOUND_TYPE_CACHE.get(stateId));
            return;
        }

        String breakSound = VisionProperties.getString(soundObject, visionData, VisionProperties.Names.BREAK, vminus$block);
        String stepSound = VisionProperties.getString(soundObject, visionData, VisionProperties.Names.STEP, vminus$block);
        String placeSound = VisionProperties.getString(soundObject, visionData, VisionProperties.Names.PLACE, vminus$block);
        String hitSound = VisionProperties.getString(soundObject, visionData, VisionProperties.Names.HIT, vminus$block);
        String fallSound = VisionProperties.getString(soundObject, visionData, VisionProperties.Names.FALL, vminus$block);

        SoundType soundType = SoundHelper.CreateBlockSoundType(breakSound, stepSound, placeSound, hitSound, fallSound);

        Visions.BLOCK_SOUND_TYPE_CACHE.put(stateId, soundType);

        cir.setReturnValue(soundType);
    }

}
