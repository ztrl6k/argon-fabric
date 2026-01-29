package dev.lvstrng.argon.gui.vapemenu.components;

import dev.lvstrng.argon.gui.vapemenu.VapeClickGui;
import dev.lvstrng.argon.module.setting.BooleanSetting;
import dev.lvstrng.argon.gui.vapemenu.utils.Animation;
import dev.lvstrng.argon.gui.vapemenu.utils.VapeRenderUtils;
import dev.lvstrng.argon.gui.vapemenu.utils.Theme;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class CheckBox extends Component {

    private final BooleanSetting setting;
    private final VapeClickGui parent;
    private float x, y;

    private final Animation animation;

    public CheckBox(BooleanSetting setting, VapeClickGui parent, float x, float y) {
        this.setting = setting;
        this.parent = parent;
        this.x = x;
        this.y = y;
        animation = new Animation(0, 100);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float delta) {
        if(setting.getValue()) {
            context.drawText(mc.textRenderer, setting.getName().toString(), (int)(x + parent.windowX + 445 + parent.settingsFieldX), (int)(y + 4), -1, false);
            animation.setEnd(100);
            Color color = new Color(33, 94, 181, (int) (animation.getValue() / 100 * 255));
            VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + parent.width - 30, y + 2, x + parent.windowX + parent.width - 10, y + 12, 4, 20, color);
            // Circle
            VapeRenderUtils.drawCircle(context, x + parent.windowX + parent.width - 25 + 10 * (animation.getValue() / 100f), y + 7, 3.5, 10, -1);
        }
        else {
            context.drawText(mc.textRenderer, setting.getName().toString(), (int)(x + parent.windowX + 445 + parent.settingsFieldX), (int)(y + 4), Theme.SLIDER_SETTING_BG.getRGB(), false);
            animation.setEnd(0);

            VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + parent.width - 30, y + 2, x + parent.windowX + parent.width - 10, y + 12, 4, 20, Theme.TOGGLE_BUTTON_BG);
            VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + parent.width - 29, y + 3, x + parent.windowX + parent.width - 11, y + 11, 3, 20, Theme.MODE_SETTING_FILL);
            // Circle
            VapeRenderUtils.drawCircle(context, x + parent.windowX + parent.width - 25 + 10 * (animation.getValue() / 100f), y + 7, 3.5, 10, Theme.TOGGLE_BUTTON_BG.getRGB());
        }

        animation.update();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if(isHovered(x + parent.windowX + parent.width - 30, y + 2, x + parent.windowX + parent.width - 10, y + 12, mouseX, mouseY)) {
            setting.setValue(!setting.getValue());
        }
    }

    public void setY(float y) {
        this.y = y;
    }
}
