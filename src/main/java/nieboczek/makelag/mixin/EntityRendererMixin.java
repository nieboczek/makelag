package nieboczek.makelag.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import nieboczek.makelag.MakeLagClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow @Final private TextRenderer textRenderer;

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundOpacity(F)F"))
    private <T extends EntityRenderState> void onSecondDraw(
            T state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci
    ) {
        if (!MakeLagClient.displayPing) {
            return;
        }

        int ping = MakeLagClient.DELAYS.getOrDefault(text.getString(), 69420);
        String pingStr = ping + "ms";

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x = ((float) -textRenderer.getWidth(pingStr)) / 2.0F;
        boolean notSneaking = !state.sneaking;

        textRenderer.draw(pingStr, x, -10, -2130706433, false, matrix, vertexConsumers, notSneaking ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 1056964608, light);
        if (notSneaking) {
            textRenderer.draw(pingStr, x, -10, -1, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2));
        }
    }
}
