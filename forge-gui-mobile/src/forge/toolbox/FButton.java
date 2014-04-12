package forge.toolbox;

import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

import forge.Forge.Graphics;
import forge.assets.FSkinColor;
import forge.assets.FSkinColor.Colors;
import forge.assets.FSkinFont;
import forge.assets.FSkinImage;
import forge.interfaces.IButton;
import forge.toolbox.FEvent.FEventHandler;
import forge.toolbox.FEvent.FEventType;

public class FButton extends FDisplayObject implements IButton {
    private static final FSkinColor FORE_COLOR = FSkinColor.get(Colors.CLR_TEXT);
    private static final float DISABLED_COMPOSITE = 0.25f;

    private FSkinImage imgL, imgM, imgR;
    private String text;
    private FSkinFont font;
    private boolean toggled = false;
    private FEventHandler command;

    public enum Corner {
        None,
        BottomLeft,
        BottomRight
    }
    private Corner corner = Corner.None;

    /**
     * Instantiates a new FButton.
     */
    public FButton() {
        this("", null);
    }

    public FButton(final String text0) {
        this(text0, null);
    }

    public FButton(final String text0, FEventHandler command0) {
        text = text0;
        command = command0;
        font = FSkinFont.get(14);
        resetImg();
    }

    private void resetImg() {
        imgL = FSkinImage.BTN_UP_LEFT;
        imgM = FSkinImage.BTN_UP_CENTER;
        imgR = FSkinImage.BTN_UP_RIGHT;
    }

    public String getText() {
        return text;
    }
    public void setText(String text0) {
        text = text0;
    }

    public void setFontSize(int fontSize0) {
        font = FSkinFont.get(fontSize0);
    }

    @Override
    public void setEnabled(boolean b0) {
        if (isEnabled() == b0) { return; }
        super.setEnabled(b0);

        if (b0) {
            resetImg();
        }
        else {
            imgL = FSkinImage.BTN_DISABLED_LEFT;
            imgM = FSkinImage.BTN_DISABLED_CENTER;
            imgR = FSkinImage.BTN_DISABLED_RIGHT;
        }
    }

    /** 
     * Button toggle state, for a "permanently pressed" functionality, e.g. as a tab.
     * 
     * @return boolean
     */
    public boolean isToggled() {
        return toggled;
    }
    public void setToggled(boolean b0) {
        if (toggled == b0) { return; }
        toggled = b0;

        if (toggled) {
            imgL = FSkinImage.BTN_TOGGLE_LEFT;
            imgM = FSkinImage.BTN_TOGGLE_CENTER;
            imgR = FSkinImage.BTN_TOGGLE_RIGHT;
        }
        else if (isEnabled()) {
            resetImg();
        }
        else {
            imgL = FSkinImage.BTN_DISABLED_LEFT;
            imgM = FSkinImage.BTN_DISABLED_CENTER;
            imgR = FSkinImage.BTN_DISABLED_RIGHT;
        }
    }

    public Corner getCorner() {
        return corner;
    }
    public void setCorner(Corner corner0) {
        corner = corner0;
    }

    public void setCommand(FEventHandler command0) {
        command = command0;
    }

    @Override
    public final boolean press(float x, float y) {
        if (isToggled()) { return true; }
        imgL = FSkinImage.BTN_DOWN_LEFT;
        imgM = FSkinImage.BTN_DOWN_CENTER;
        imgR = FSkinImage.BTN_DOWN_RIGHT;
        return true;
    }

    @Override
    public final boolean release(float x, float y) {
        if (isToggled()) { return true; }
        resetImg();
        return true;
    }

    @Override
    public final boolean tap(float x, float y, int count) {
        if (count == 1 && command != null) {
            command.handleEvent(new FEvent(this, FEventType.TAP));
        }
        return true;
    }

    @Override
    public void draw(Graphics g) {
        float x = 0;
        float y = 0;
        float w = getWidth();
        float h = getHeight();

        float cornerButtonWidth = w / 2;
        float cornerButtonHeight = h * 1.5f;
        float cornerTextOffsetX = cornerButtonWidth / 2;
        float cornerTextOffsetY = (cornerButtonHeight - h) / 2;

        boolean disabled = !isEnabled();
        if (disabled) {
            g.setAlphaComposite(DISABLED_COMPOSITE);
        }

        //determine images to draw and text alignment based on which corner button is in (if any)
        switch (corner) {
        case None:
            if (w > 2 * h) {
                g.drawImage(imgL, 0, 0, h, h);
                g.drawImage(imgM, h, 0, w - (2 * h), h);
                g.drawImage(imgR, w - h, 0, h, h);
            }
            else {
                g.drawImage(imgL, 0, 0, cornerButtonWidth, h);
                g.drawImage(imgR, cornerButtonWidth, 0, w - cornerButtonWidth, h);
            }
            break;
        case BottomLeft:
            g.drawImage(imgM, 0, 0, cornerButtonWidth, cornerButtonHeight);
            g.drawImage(imgR, cornerButtonWidth, 0, cornerButtonWidth, cornerButtonHeight);
            w -= cornerTextOffsetX;
            y += cornerTextOffsetY;
            h -= cornerTextOffsetY;
            break;
        case BottomRight:
            g.drawImage(imgL, 0, 0, cornerButtonWidth, cornerButtonHeight);
            g.drawImage(imgM, cornerButtonWidth, 0, cornerButtonWidth, cornerButtonHeight);
            x += cornerTextOffsetX;
            w -= cornerTextOffsetX;
            y += cornerTextOffsetY;
            h -= cornerTextOffsetY;
            break;
        }

        if (!StringUtils.isEmpty(text)) {
            g.drawText(text, font, FORE_COLOR, x, y, w, h, false, HAlignment.CENTER, true);
        }

        if (disabled) {
            g.resetAlphaComposite();
        }
    }

    @Override
    public boolean isSelected() {
        return isToggled();
    }

    @Override
    public void setSelected(boolean b0) {
        setToggled(b0);
    }
}