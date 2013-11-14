package forge.gui.deckeditor.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import forge.gui.WrapLayout;
import forge.gui.deckeditor.controllers.CCurrentDeck;
import forge.gui.framework.DragCell;
import forge.gui.framework.DragTab;
import forge.gui.framework.EDocID;
import forge.gui.framework.IVDoc;
import forge.gui.toolbox.FLabel;
import forge.gui.toolbox.FSkin;
import forge.gui.toolbox.FTextField;
import forge.gui.toolbox.itemmanager.ItemManager;
import forge.gui.toolbox.itemmanager.ItemManagerContainer;
import forge.gui.toolbox.itemmanager.SItemManagerUtil;
import forge.item.InventoryItem;


/** 
 * Assembles Swing components of current deck being edited in deck editor.
 *
 * <br><br><i>(V at beginning of class name denotes a view class.)</i>
 */
public enum VCurrentDeck implements IVDoc<CCurrentDeck> {
    /** */
    SINGLETON_INSTANCE;

    // Fields used with interface IVDoc
    private DragCell parentCell;
    private final DragTab tab = new DragTab("Current Deck");

    // Other fields

    private final JLabel btnSave = new FLabel.Builder()
            .fontSize(14)
            .tooltip("Save (in default directory)")
            .iconInBackground(true)
            .iconAlignX(SwingConstants.CENTER)
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_SAVE))
            .text(" ").hoverable(true).build();

    private final JLabel btnExport = new FLabel.Builder()
            .fontSize(14)
            .tooltip("Save As")
            .iconInBackground(true)
            .iconAlignX(SwingConstants.CENTER)
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_SAVEAS))
            .text(" ").hoverable(true).build();

    private final JLabel btnLoad = new FLabel.Builder()
            .fontSize(14)
            .tooltip("Load")
            .iconInBackground(true)
            .iconAlignX(SwingConstants.CENTER)
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_OPEN))
            .text(" ").hoverable(true).build();

    private final JLabel btnNew = new FLabel.Builder()
            .fontSize(14)
            .tooltip("New Deck")
            .iconInBackground(true)
            .iconAlignX(SwingConstants.CENTER)
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_NEW))
            .text(" ").hoverable(true).build();

    private final JLabel btnPrintProxies = new FLabel.Builder()
            .fontSize(14)
            .tooltip("Print to HTML file")
            .iconInBackground(true)
            .iconAlignX(SwingConstants.CENTER)
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_PRINT))
            .text(" ").hoverable(true).build();

    private final JPanel pnlRemoveButtons =
            new JPanel(new MigLayout("insets 0, gap 0, ax center, hidemode 3"));

    private final FLabel btnRemove = new FLabel.Builder()
            .fontSize(14)
            .text("Remove card")
            .tooltip("Remove selected card from current deck (or double click the row or hit the spacebar)")
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_MINUS))
            .iconScaleAuto(false).hoverable().build();

    private final FLabel btnRemove4 = new FLabel.Builder()
            .fontSize(14)
            .text("Remove 4 of card")
            .tooltip("Remove up to 4 of selected card to current deck")
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_MINUS))
            .iconScaleAuto(false).hoverable().build();

    private final JLabel btnCycleSection = new FLabel.Builder()
            .fontSize(14)
            .text("Change Section")
            .tooltip("Toggle between editing the deck and the sideboard/planar/scheme/vanguard parts of this deck")
            .icon(FSkin.getIcon(FSkin.InterfaceIcons.ICO_EDIT))
            .iconScaleAuto(false).hoverable().build();

    private final FLabel btnImport = new FLabel.Builder()
            .fontSize(14)
            .text("Import").tooltip("Attempt to import a deck from a non-Forge format")
            .opaque(true).hoverable(true).build();

    
    private final FTextField txfTitle = new FTextField.Builder().ghostText("[New Deck]").build();

    private final JPanel pnlRemove = new JPanel();
    private final JPanel pnlHeader = new JPanel();

    private final JLabel lblTitle = new FLabel.Builder().text("Title")
            .fontSize(14).build();

    // Total and color count labels/filter toggles
    private final JPanel pnlStats = new JPanel(new WrapLayout(FlowLayout.LEFT));
    private final Map<SItemManagerUtil.StatTypes, FLabel> statLabels =
            new HashMap<SItemManagerUtil.StatTypes, FLabel>();

    private final ItemManagerContainer itemManagerContainer = new ItemManagerContainer();
    private ItemManager<? extends InventoryItem> itemManager;

    //========== Constructor

    private VCurrentDeck() {
        // Header area
        pnlHeader.setOpaque(false);
        pnlHeader.setLayout(new MigLayout("insets 0, gap 0, ax center, hidemode 3"));

        pnlHeader.add(lblTitle, "w 80px!, h 30px!, gap 5px 5px 0 0");
        pnlHeader.add(txfTitle, "pushx, growx, gap 0 5px 0 0");
        pnlHeader.add(btnSave, "w 26px!, h 26px!, gap 0 5px 0 0");
        pnlHeader.add(btnNew, "w 26px!, h 26px!, gap 0 5px 0 0");

        pnlHeader.add(btnLoad, "w 26px!, h 26px!, gap 0 5px 0 0");
        pnlHeader.add(btnExport, "w 26px!, h 26px!, gap 0 5px 0 0");
        pnlHeader.add(btnPrintProxies, "w 26px!, h 26px!, gap 0 5px 0 0");
        pnlHeader.add(btnImport, "w 50px!, h 26px!, gap 0 0 20px 0");

        pnlRemove.setOpaque(false);
        pnlRemove.setLayout(new MigLayout("insets 0, gap 0, ax center"));
        pnlRemove.add(btnRemove, "w 30%!, h 30px!, gap 10 10 5 5");
        pnlRemove.add(btnRemove4, "w 30%!, h 30px!, gap 10 10 5 5");
        pnlRemove.add(btnCycleSection, "w 30%!, h 30px!, gap 10 10 5 5");

        pnlStats.setOpaque(false);
        
        for (SItemManagerUtil.StatTypes s : SItemManagerUtil.StatTypes.values()) {
            FLabel label = buildLabel(s);
            statLabels.put(s, label);
            if (SItemManagerUtil.StatTypes.PACK == s) {
                pnlStats.add(buildLabel(null));
            } else {
                pnlStats.add(label);
            }
        }

        pnlRemoveButtons.setOpaque(false);
        pnlRemoveButtons.add(btnRemove, "w 30%!, h 30px!, gap 0 0 5px 5px");
        pnlRemoveButtons.add(btnRemove4, "w 30%!, h 30px!, gap 0 0 5px 5px");
        pnlRemoveButtons.add(btnCycleSection, "w 30%!, h 30px!, gap 0 0 5px 5px");
    }

    //========== Overridden from IVDoc

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getDocumentID()
     */
    @Override
    public EDocID getDocumentID() {
        return EDocID.EDITOR_CURRENTDECK;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getTabLabel()
     */
    @Override
    public DragTab getTabLabel() {
        return tab;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getLayoutControl()
     */
    @Override
    public CCurrentDeck getLayoutControl() {
        return CCurrentDeck.SINGLETON_INSTANCE;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#setParentCell(forge.gui.framework.DragCell)
     */
    @Override
    public void setParentCell(final DragCell cell0) {
        this.parentCell = cell0;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getParentCell()
     */
    @Override
    public DragCell getParentCell() {
        return this.parentCell;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#populate()
     */
    @Override
    public void populate() {
        final JPanel parentBody = parentCell.getBody();
        parentBody.setLayout(new MigLayout("insets 0, gap 0, wrap, hidemode 3, center"));
        parentBody.add(pnlHeader, "w 98%!, gap 1% 1% 5 0");
        parentBody.add(pnlStats, "w 100:500:500, center");
        parentBody.add(pnlRemoveButtons, "w 96%!, gap 2% 0 0 0");
        parentBody.add(itemManagerContainer, "w 98%!, h 100%  - 35, gap 1% 0 0 1%");
    }
    
    public ItemManager<? extends InventoryItem> getItemManager() {
        return this.itemManager;
    }
    
    public void setItemManager(final ItemManager<? extends InventoryItem> itemManager0) {
        this.itemManager = itemManager0;
        itemManagerContainer.setItemManager(itemManager0);
    }
    
    public Map<SItemManagerUtil.StatTypes, FLabel> getStatLabels() {
        return statLabels;
    }

    public JLabel getLblTitle() { return lblTitle; }

    //========== Retrieval

    /** @return {@link javax.swing.JLabel} */
    public FLabel getBtnRemove() {
        return btnRemove;
    }

    /** @return {@link javax.swing.JLabel} */
    public FLabel getBtnRemove4() {
        return btnRemove4;
    }

    /** @return {@link javax.swing.JLabel} */
    public JLabel getBtnSave() {
        return btnSave;
    }

    /** @return {@link javax.swing.JLabel} */
    public JLabel getBtnSaveAs() {
        return btnExport;
    }

    /** @return {@link javax.swing.JLabel} */
    public JLabel getBtnPrintProxies() {
        return btnPrintProxies;
    }

    /** @return {@link javax.swing.JLabel} */
    public JLabel getBtnOpen() {
        return btnLoad;
    }

    /** @return {@link javax.swing.JLabel} */
    public JLabel getBtnNew() {
        return btnNew;
    }

    /** @return {@link forge.gui.toolbar.FTextField} */
    public FTextField getTxfTitle() {
        return txfTitle;
    }

    /** @return {@link javax.swing.JPanel} */
    public JPanel getPnlHeader() {
        return pnlHeader;
    }

    /** @return {@link javax.swing.JPanel} */
    public void setStatsVisible(boolean val) {
        pnlStats.setVisible(val);
        
        // TODO: invisibly ensure the cell is not too large on first show
        // if the program is started with pnlStats invisible, the first time it
        // is made visible, the scroller cell that surrounds the panel will
        // be too large.  it will jump back to the correct size after any resize
        // event, or if it is hidden and made visible again.  For the life of
        // me, I cannot figure out how to "kick" it into the correct size from
        // here without the following hack, which will cause a visible jitter
        // when the panel is shown for the first time (subsequent hides and shows
        // will not cause any jitter), but at least it will be the correct size:
        if (val) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    pnlStats.setVisible(false);
                    pnlStats.setVisible(true);
                }
            });
        }
    }

    /** @return {@link javax.swing.JPanel} */
    public JPanel getPnlRemButtons() {
        return pnlRemoveButtons;
    }

    /** @return {@link javax.swing.JPanel} */
    public JLabel getBtnDoSideboard() {
        return btnCycleSection;
    }

    //========== Other methods

    private FLabel buildLabel(SItemManagerUtil.StatTypes s) {
        FLabel label = new FLabel.Builder()
            .icon(s == null ? null : s.img).iconScaleAuto(false)
            .fontSize(11).tooltip(s == null ? null : s.toLabelString())
            .build();

        Dimension labelSize = new Dimension(57, 20);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        
        return label;
    }

    /**
     * TODO: Write javadoc for this method.
     * @return
     */
    public FLabel getBtnImport() {
        // TODO Auto-generated method stub
        return (FLabel) btnImport;
    }
}