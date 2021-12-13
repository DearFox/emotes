package io.github.kosmx.emotes.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity;
import io.github.kosmx.emotes.executor.emotePlayer.IUpperPartHelper;
import io.github.kosmx.emotes.fabric.BendableModelPart;
import io.github.kosmx.emotes.arch.emote.EmotePlayImpl;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderRedirect<T extends Entity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRenderRedirect(EntityRendererProvider.Context context) {
        super(context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"))
    private void featureRendererTransformer(RenderLayer<T, M> featureRenderer, PoseStack matrices, MultiBufferSource vertexConsumers, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch){
        if(livingEntity instanceof Player && livingEntity instanceof IEmotePlayerEntity && ((IEmotePlayerEntity<EmotePlayImpl>) livingEntity).isPlayingEmote() && ((IUpperPartHelper) featureRenderer).isUpperPart()){
            matrices.pushPose();
            BendableModelPart.roteteMatrixStack(matrices, ((IEmotePlayerEntity<EmotePlayImpl>) livingEntity).getEmote().torso.getBend());
            featureRenderer.render(matrices, vertexConsumers, light, livingEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            matrices.popPose();
        }else{
            featureRenderer.render(matrices, vertexConsumers, light, livingEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
    }

}
