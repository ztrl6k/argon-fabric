package dev.lvstrng.argon.gui.vapemenu.components;

import dev.lvstrng.argon.gui.vapemenu.VapeClickGui;
import dev.lvstrng.argon.module.setting.NumberSetting;
import dev.lvstrng.argon.gui.vapemenu.utils.VapeRenderUtils;
import dev.lvstrng.argon.gui.vapemenu.utils.Theme;
import net.minecraft.client.gui.DrawContext;

public class Slider extends Component {

    private final NumberSetting setting;
    private final VapeClickGui parent;
    private float x, y;
    private boolean dragging = false;

    public Slider(NumberSetting setting, float x, float y, VapeClickGui parent) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float delta) {

        float present = (float) (((x + parent.windowX + parent.width - 11) - (x + parent.windowX + 450 + parent.settingsFieldX))
                * (setting.getValue() - setting.getMin())
                / (setting.getMax() - setting.getMin()));

        // Setting Name
        if(dragging) {
            context.drawText(mc.textRenderer, setting.getName().toString(), (int)(parent.windowX + 445 + parent.settingsFieldX), (int)(y + 5), -1, false);
        }
        else {
            context.drawText(mc.textRenderer, setting.getName().toString(), (int)(parent.windowX + 445 + parent.settingsFieldX), (int)(y + 5), Theme.MODULE_TEXT.getRGB(), false);
        }

        // Value
        context.drawText(mc.textRenderer, String.valueOf(setting.getValue()), (int)(parent.windowX + parent.width - 20), (int)(y + 5), Theme.NORMAL_TEXT_COLOR.getRGB(), false);
        // Bg
        VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + 450 + parent.settingsFieldX, y + 20, x + parent.windowX + parent.width - 11, y + 21.5f, 1, 20, Theme.SLIDER_SETTING_BG);
        // Slider itself
        VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + 450 + parent.settingsFieldX, y + 20, x + parent.windowX + 450 + parent.settingsFieldX + present, y + 21.5f, 1, 20, Theme.ENABLED);

        if(dragging) {
            double render2 = setting.getMin();
            double max = setting.getMax();
            double inc = 0.1;
            double valAbs = (double) mouseX - ((double) (x + parent.windowX + 450 + parent.settingsFieldX));
            double perc = valAbs / (((x + parent.windowX + parent.width - 11) - (x + parent.windowX + 450 + parent.settingsFieldX)));
            perc = Math.min(Math.max(0.0D, perc), 1.0D);
            double valRel = (max - render2) * perc;
            double val = render2 + valRel;
            val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
            setting.setValue(val);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if(isHovered(x + parent.windowX + 450 + parent.settingsFieldX, y + 18, x + parent.windowX + parent.width - 11, y + 23.5f, mouseX, mouseY)) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
