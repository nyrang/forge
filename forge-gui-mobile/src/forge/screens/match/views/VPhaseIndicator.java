package forge.screens.match.views;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

import forge.Forge.Graphics;
import forge.assets.FSkinColor;
import forge.assets.FSkinFont;
import forge.assets.FSkinColor.Colors;
import forge.game.phase.PhaseType;
import forge.toolbox.FContainer;
import forge.toolbox.FDisplayObject;
import forge.util.Utils;

public class VPhaseIndicator extends FContainer {
    private static final FSkinFont BASE_FONT = FSkinFont.get(11);
    private static final float PADDING_X = Utils.scaleX(1);
    private static final float PADDING_Y = Utils.scaleY(2);

    private final Map<PhaseType, PhaseLabel> phaseLabels = new HashMap<PhaseType, PhaseLabel>();
    private FSkinFont font;

    public VPhaseIndicator() {
        addPhaseLabel("UP", PhaseType.UPKEEP);
        addPhaseLabel("DR", PhaseType.DRAW);
        addPhaseLabel("M1", PhaseType.MAIN1);
        addPhaseLabel("BC", PhaseType.COMBAT_BEGIN);
        addPhaseLabel("DA", PhaseType.COMBAT_DECLARE_ATTACKERS);
        addPhaseLabel("DB", PhaseType.COMBAT_DECLARE_BLOCKERS);
        addPhaseLabel("FS", PhaseType.COMBAT_FIRST_STRIKE_DAMAGE);
        addPhaseLabel("CD", PhaseType.COMBAT_DAMAGE);
        addPhaseLabel("EC", PhaseType.COMBAT_END);
        addPhaseLabel("M2", PhaseType.MAIN2);
        addPhaseLabel("ET", PhaseType.END_OF_TURN);
        addPhaseLabel("CL", PhaseType.CLEANUP);
    }

    private void addPhaseLabel(String caption, PhaseType phaseType) {
        phaseLabels.put(phaseType, add(new PhaseLabel(caption, phaseType)));
    }

    public PhaseLabel getLabel(PhaseType phaseType) {
        return phaseLabels.get(phaseType);
    }

    public void resetPhaseButtons() {
        for (PhaseLabel lbl : phaseLabels.values()) {
            lbl.setActive(false);
        }
    }

    public float getPreferredHeight(float width) {
        //build string to use to determine ideal font
        float w = width / phaseLabels.size();
        w -= 2 * PADDING_X;
        font = BASE_FONT;
        return _getPreferredHeight(w);
    }
    private float _getPreferredHeight(float w) {
        TextBounds bounds = null;
        for (PhaseLabel lbl : phaseLabels.values()) {
            bounds = font.getBounds(lbl.caption);
            if (bounds.width > w) {
                if (font.canShrink()) {
                    font = font.shrink();
                    return _getPreferredHeight(w);
                }
                break;
            }
        }
        return bounds.height + 2 * PADDING_Y;
    }

    @Override
    protected void doLayout(float width, float height) {
        float x = 0;
        float w = width / phaseLabels.size();
        float h = height;

        for (FDisplayObject lbl : getChildren()) {
            lbl.setBounds(x, 0, w, h);
            x += w;
        }
    }

    public class PhaseLabel extends FDisplayObject {
        private final String caption;
        private final PhaseType phaseType;
        private boolean stopAtPhase = false;
        private boolean active = false;

        public PhaseLabel(String caption0, PhaseType phaseType0) {
            caption = caption0;
            phaseType = phaseType0;
        }

        public boolean getActive() {
            return active;
        }
        public void setActive(boolean active0) {
            active = active0;
        }

        public PhaseType getPhaseType() {
            return phaseType;
        }

        public boolean getStopAtPhase() {
            return stopAtPhase;
        }
        public void setStopAtPhase(boolean stopAtPhase0) {
            stopAtPhase = stopAtPhase0;
        }

        @Override
        public boolean tap(float x, float y, int count) {
            stopAtPhase = !stopAtPhase;
            return true;
        }

        @Override
        public void draw(final Graphics g) {
            float x = PADDING_X;
            float w = getWidth() - 2 * PADDING_X;
            float h = getHeight();

            //determine back color according to skip or active state of label
            FSkinColor backColor;
            if (active && stopAtPhase) {
                backColor = FSkinColor.get(Colors.CLR_PHASE_ACTIVE_ENABLED);
            }
            else if (!active && stopAtPhase) {
                backColor = FSkinColor.get(Colors.CLR_PHASE_INACTIVE_ENABLED);
            }
            else if (active && !stopAtPhase) {
                backColor = FSkinColor.get(Colors.CLR_PHASE_ACTIVE_DISABLED);
            }
            else {
                backColor = FSkinColor.get(Colors.CLR_PHASE_INACTIVE_DISABLED);
            }
            g.fillRect(backColor, x, 0, w, h);
            g.drawText(caption, font, Color.BLACK, x, PADDING_Y, w, h, false, HAlignment.CENTER, false);
        }
    }
}
