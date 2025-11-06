package nieboczek.makelag.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.MakeLagClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends EntityRenderer<PlayerEntity, PlayerEntityRenderState> {
    private PlayerEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("TAIL")
    )
    private void drawPing(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!MakeLagClient.displayPing) {
            return;
        }

        TextRenderer textRenderer = getTextRenderer();
        int ping = MakeLagClient.DELAYS.getOrDefault(text.getString(), 1000);
        String pingStr = ping + "ms";

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x = -textRenderer.getWidth(pingStr) / 2f;
        boolean notSneaking = !state.sneaking;

        textRenderer.draw(pingStr, x, -10, -2130706433, false, matrix, vertexConsumers, notSneaking ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 1056964608, light);
        if (notSneaking) {
            textRenderer.draw(pingStr, x, -10, -1, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2));
        }
    }
}
