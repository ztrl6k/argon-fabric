package dev.lvstrng.argon.gui.vapemenu.components;

import dev.lvstrng.argon.gui.vapemenu.VapeClickGui;
import dev.lvstrng.argon.module.setting.ModeSetting;
import dev.lvstrng.argon.gui.vapemenu.utils.VapeRenderUtils;
import dev.lvstrng.argon.gui.vapemenu.utils.Theme;
import net.minecraft.client.gui.DrawContext;

public class ModeComp extends Component {

    private final ModeSetting setting;
    private final VapeClickGui parent;
    private float x, y;

    public ModeComp(ModeSetting setting, VapeClickGui parent, float x, float y) {
        this.setting = setting;
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float delta) {
        VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + 445 + parent.settingsFieldX, y + 2, x + parent.windowX + parent.width - 5, y + 22, 2, 20, Theme.MODE_SETTING_BG);
        VapeRenderUtils.renderRoundedQuad(context, x + parent.windowX + 446 + parent.settingsFieldX, y + 3, x + parent.windowX + parent.width - 6, y + 21, 2, 20, Theme.MODE_SETTING_FILL);
        context.drawText(mc.textRenderer, setting.getName().toString() + ": " + setting.getMode(), (int)(x + parent.windowX + 455 + parent.settingsFieldX), (int)(y + 10), Theme.NORMAL_TEXT_COLOR.getRGB(), false);
        context.drawText(mc.textRenderer, ">", (int)(x + parent.windowX + parent.width - 15), (int)(y + 9), Theme.MODULE_TEXT.getRGB(), false);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        if(isHovered(x + parent.windowX + 445 + parent.settingsFieldX, y + 2, x + parent.windowX + parent.width - 5, y + 22, mouseX, mouseY) && button == 0) {
            setting.cycle();
        }
    }

    public void setY(float y) {
        this.y = y;
    }
}
