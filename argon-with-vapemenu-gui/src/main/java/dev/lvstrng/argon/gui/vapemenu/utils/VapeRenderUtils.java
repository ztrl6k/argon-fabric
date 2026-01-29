package dev.lvstrng.argon.gui.vapemenu.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

// Helper class for drawing specific things to the screen (adapted for MC 1.21)
public class VapeRenderUtils {

    /**
     * Fills a rectangle with the specified color
     * Adapted for DrawContext in MC 1.21
     */
    public static void fill(DrawContext context, double x1, double y1, double x2, double y2, int color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        double i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float) (color >> 24 & 0xFF) / 255.0f;
        float g = (float) (color >> 16 & 0xFF) / 255.0f;
        float h = (float) (color >> 8 & 0xFF) / 255.0f;
        float j = (float) (color & 0xFF) / 255.0f;
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0f).color(g, h, j, f);
        
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawHollowRect(DrawContext context, int x, int y, int width, int height, int color, int thickness) {
        fill(context, x, y - thickness, x - thickness, y + height + thickness, color);
        fill(context, x + width, y - thickness, x + width + thickness, y + height + thickness, color);
        fill(context, x, y, x + width, y - thickness, color);
        fill(context, x, y + height, x + width, y + height + thickness, color);
    }

    /**
     * Renders a rounded quad
     * Credit to Coffee client
     */
    public static void renderRoundedQuad(DrawContext context, double fromX, double fromY, double toX, double toY, double rad, double samples, Color c) {
        int color = c.getRGB();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][]{new double[]{toX1, toY1}, new double[]{toX1, fromY1}, new double[]{fromX1, fromY1}, new double[]{fromX1, toY1}};
        
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
            }
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    /**
     * Wrapper method for drawCircle with Color object
     */
    public static void drawCircle(DrawContext context, double centerX, double centerY, double radius, double samples, Color color) {
        drawCircle(context, centerX, centerY, radius, samples, color.getRGB());
    }

    /**
     * Draws a circle
     */
    public static void drawCircle(DrawContext context, double centerX, double centerY, double radius, double samples, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        for (double r = 0; r < 360; r += (360 / samples)) {
            float rad1 = (float) Math.toRadians(r);
            float sin = (float) (Math.sin(rad1) * radius);
            float cos = (float) (Math.cos(rad1) * radius);
            bufferBuilder.vertex(matrix, (float) centerX + sin, (float) centerY + cos, 0.0F).color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    /**
     * Better scissor implementation
     */
    public static void betterScissor(double x, double y, double x2, double y2) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int xPercent = (int) (x / mc.getWindow().getScaledWidth());
        int yPercent = (int) (y / mc.getWindow().getHeight());
        int widthPercent = (int) (x2 / mc.getWindow().getWidth());
        int heightPercent = (int) (y2 / mc.getWindow().getHeight());
        RenderSystem.enableScissor(xPercent, yPercent, widthPercent, heightPercent);
    }

    /**
     * Draws an image at specified coords
     */
    public static void drawTexturedRectangle(DrawContext context, float x, float y, String path) {
        RenderSystem.setShaderTexture(0, Identifier.of("argon", path));
        try {
            URL url = VapeRenderUtils.class.getResource("/assets/argon/" + path);
            BufferedImage image = ImageIO.read(url);
            context.drawTexture(Identifier.of("argon", path), (int) x, (int) y, 0.0f, 0.0f, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws a scaled textured rectangle
     */
    public static void drawScaledTexturedRect(DrawContext context, float x, float y, float scale, String path) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 0);
        
        RenderSystem.setShaderTexture(0, Identifier.of("argon", path));
        try {
            URL url = VapeRenderUtils.class.getResource("/assets/argon/" + path);
            BufferedImage image = ImageIO.read(url);
            context.drawTexture(Identifier.of("argon", path), (int) (x / scale), (int) (y / scale), 0.0f, 0.0f, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());
            context.getMatrices().pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
