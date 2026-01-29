package dev.lvstrng.argon.gui.vapemenu;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lvstrng.argon.gui.vapemenu.components.ModButton;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.gui.vapemenu.utils.VapeRenderUtils;
import dev.lvstrng.argon.gui.vapemenu.utils.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class VapeClickGui extends Screen {

    private static final VapeClickGui INSTANCE = new VapeClickGui();
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Dragging
    private float dragX, dragY;
    private boolean drag = false;

    // Window properties
    public float windowX = 200, windowY = 200;
    public float width = 500, height = 310;
    
    // Selected items
    public Category selectedCategory = Category.COMBAT;
    public Module selectedModule;

    // Animation values
    private float percent = 1.0f, lastPercent = 1.0f;
    private float percent2 = 1.0f, lastPercent2 = 1.0f;
    private boolean close = false;

    // UI elements
    public final List<ModButton> modButtons = new ArrayList<>();
    public int coordModX = 0;
    public float settingsFieldX;
    public float settingsFNow, settingsF;
    
    // Scrolling
    private float modScrollEnd, modScrollNow;
    private double dWheel;

    protected VapeClickGui() {
        super(Text.of("Argon ClickGUI"));
    }

    public static VapeClickGui getInstance() {
        return INSTANCE;
    }

    @Override
    protected void init() {
        Theme.darkTheme(); // Initialize theme
        
        percent = 1.0f;
        lastPercent = 1.0f;
        percent2 = 1.0f;
        lastPercent2 = 1.0f;
        drag = false;
        selectedModule = null;
        modButtons.clear();

        // Create module buttons
        float modY = 70 + modScrollNow;
        for(Module module : Argon.INSTANCE.moduleManager.getModules()) {
            if(module.getCategory() == selectedCategory) {
                modButtons.add(new ModButton(module, 0, modY, this));
                modY += 40;
            }
        }

        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Handle animations
        percent = smoothTrans(percent, lastPercent);
        percent2 = smoothTrans(percent2, lastPercent2);

        // Scale window
        if (percent > 0.98) {
            context.getMatrices().push();
            context.getMatrices().translate(mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2, 0);
            context.getMatrices().scale(percent, percent, 0);
            context.getMatrices().translate(-mc.getWindow().getScaledWidth() / 2, -mc.getWindow().getScaledHeight() / 2, 0);
        }

        // Handle dragging
        if(drag) {
            if (dragX == 0 && dragY == 0) {
                dragX = mouseX - windowX;
                dragY = mouseY = windowY;
            } else {
                windowX = mouseX - dragX;
                windowY = mouseY - dragY;
            }
        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
        }

        // Draw main window
        VapeRenderUtils.renderRoundedQuad(context, windowX, windowY, windowX + width, windowY + height, 25, 20, Theme.WINDOW_COLOR);

        // Draw title
        context.getMatrices().push();
        context.getMatrices().scale(2, 2, 0);
        context.drawText(mc.textRenderer, "Argon", (int)((windowX + 20) / 2), (int)((windowY + 20) / 2), -1, false);
        context.getMatrices().pop();

        // Enable scissor for scrolling area
        RenderSystem.enableScissor(
            (int)(windowX + 5) * 2, 
            (int)(-windowY + (height / 2) + 55) * 2, 
            mc.getWindow().getScaledWidth() + 40, 
            mc.getWindow().getScaledHeight() - 10
        );

        // Draw categories (only when no module is expanded)
        if(selectedModule == null) {
            float cateY = windowY + 65;

            for (Category category : Category.values()) {
                if (category == selectedCategory) {
                    context.drawText(mc.textRenderer, category.name.toString(), (int)(windowX + 20), (int)cateY, -1, false);
                } else {
                    context.drawText(mc.textRenderer, category.name.toString(), (int)(windowX + 20), (int)cateY, Theme.UNFOCUSED_TEXT_COLOR.getRGB(), false);
                }
                cateY += 20;
            }
        }

        // Disable scissor
        RenderSystem.disableScissor();

        // Handle scrolling
        if(dWheel != 0) {
            modScrollEnd += dWheel * 10;
            dWheel = 0;
        }

        // Clamp scroll
        if(modScrollEnd > 0) modScrollEnd = 0;
        
        // Smooth scroll
        modScrollNow += (modScrollEnd - modScrollNow) / 10f;

        // Update module button positions
        float modY = 70 + modScrollNow;
        for(ModButton button : modButtons) {
            button.setY(modY);
            modY += 40;
        }

        // Enable scissor for module list
        RenderSystem.enableScissor(
            (int)(windowX + 100 + settingsFieldX) * 2,
            (int)(windowY + 60) * 2,
            (int)(325 + coordModX) * 2,
            (int)(height - 60) * 2
        );

        // Draw module buttons
        for(ModButton button : modButtons) {
            button.drawScreen(context, mouseX, mouseY, delta);
        }

        // Disable scissor
        RenderSystem.disableScissor();

        // Settings animation
        if(selectedModule != null) {
            settingsF = 325;
            coordModX = -325;
        } else {
            settingsF = 0;
            coordModX = 0;
        }

        settingsFNow += (settingsF - settingsFNow) / 10f;
        settingsFieldX = settingsFNow;

        // Pop the scale transform
        if (percent > 0.98) {
            context.getMatrices().pop();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking on window header to drag
        if(isHovered(windowX, windowY, windowX + width, windowY + 55, mouseX, mouseY) && button == 0) {
            drag = true;
            return true;
        }

        // Check category clicks (only when no module expanded)
        if(selectedModule == null) {
            float cateY = windowY + 65;
            for(Category category : Category.values()) {
                if(isHovered(windowX + 20, cateY, windowX + 20 + mc.textRenderer.getWidth(category.name.toString()), cateY + 10, mouseX, mouseY) && button == 0) {
                    selectedCategory = category;
                    // Reload modules for this category
                    modButtons.clear();
                    float modY = 70;
                    for(Module module : Argon.INSTANCE.moduleManager.getModules()) {
                        if(module.getCategory() == selectedCategory) {
                            modButtons.add(new ModButton(module, 0, modY, this));
                            modY += 40;
                        }
                    }
                    modScrollEnd = 0;
                    modScrollNow = 0;
                    return true;
                }
                cateY += 20;
            }
        }

        // Forward to module buttons
        for(ModButton mb : modButtons) {
            mb.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        drag = false;
        
        for(ModButton mb : modButtons) {
            mb.mouseReleased(mouseX, mouseY, button);
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(isHovered(windowX + 100 + settingsFieldX, windowY + 60, windowX + 425 + settingsFieldX, windowY + height, mouseX, mouseY)) {
            this.dWheel = amount;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for(ModButton mb : modButtons) {
            mb.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for(ModButton mb : modButtons) {
            mb.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    private float smoothTrans(float current, float last) {
        return last + (current - last) * 0.125f;
    }

    public boolean isHovered(float x, float y, float x2, float y2, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}
