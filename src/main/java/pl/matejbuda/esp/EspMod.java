package pl.matejbuda.esp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;

public class EspMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("[SimpleESP] Mod załadowany!");
        
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null || mc.player == null) return;

            MatrixStack matrices = context.matrixStack();
            float tickDelta = context.tickDelta();
            
            // Pozycja kamery
            var cameraPos = context.camera().getPos();

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.lineWidth(2.5f);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            mc.world.getPlayers().forEach(player -> {
                if (player == mc.player || !player.isAlive() || player.isSpectator()) return;

                // Interpolowana pozycja gracza
                double x = player.prevX + (player.getX() - player.prevX) * tickDelta - cameraPos.x;
                double y = player.prevY + (player.getY() - player.prevY) * tickDelta - cameraPos.y;
                double z = player.prevZ + (player.getZ() - player.prevZ) * tickDelta - cameraPos.z;

                matrices.push();
                matrices.translate(x, y, z);

                Box box = player.getBoundingBox().offset(-player.getX(), -player.getY(), -player.getZ());
                Matrix4f matrix = matrices.peek().getPositionMatrix();

                // Rysuj czerwony box
                drawBox(buffer, matrix, box, 1.0f, 0.0f, 0.0f, 1.0f);

                matrices.pop();
            });

            BufferRenderer.drawWithGlobalProgram(buffer.end());

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.lineWidth(1.0f);
        });
    }

    private void drawBox(BufferBuilder buffer, Matrix4f matrix, Box box, float r, float g, float b, float a) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Dolna podstawa
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);

        // Górna podstawa
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        // Boki (pionowe)
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
    }
}