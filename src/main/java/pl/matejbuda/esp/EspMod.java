// src/main/java/pl/matejbuda/esp/EspMod.java
package pl.matejbuda.esp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import com.mojang.blaze3d.systems.RenderSystem;

public class EspMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null || mc.player == null) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            float tickDelta = context.tickDelta();

            // wyłączenie testu głębi = widzimy przez ściany
            RenderSystem.disableDepthTest();
            RenderSystem.lineWidth(2.5f);

            VertexConsumer vertexConsumer = mc.getBufferBuilders()
                .getEntityVertexConsumers()
                .getBuffer(RenderLayer.getLines());

            mc.world.getPlayers()
                .stream()
                .filter(p -> p != mc.player && p.isAlive() && !p.isSpectator())
                .forEach(player -> {
                    double x = player.getLerpedPos(tickDelta).x - camera.getPos().x;
                    double y = player.getLerpedPos(tickDelta).y - camera.getPos().y;
                    double z = player.getLerpedPos(tickDelta).z - camera.getPos().z;

                    matrices.push();
                    matrices.translate(x, y, z);

                    Box bb = player.getBoundingBox()
                        .offset(-player.getX(), -player.getY(), -player.getZ()); // przesunięcie do pozycji względnej

                    // czerwony outline dla graczy
                    WorldRenderer.drawBox(
                        matrices,
                        vertexConsumer,
                        bb,
                        1.0f, 0.0f, 0.0f, 1.0f
                    );

                    matrices.pop();
                });

            // rysujemy wszystko naraz na końcu
            mc.getBufferBuilders().getEntityVertexConsumers().draw(RenderLayer.getLines());

            // przywracamy normalne renderowanie
            RenderSystem.enableDepthTest();
            RenderSystem.lineWidth(1.0f);
        });
    }
}