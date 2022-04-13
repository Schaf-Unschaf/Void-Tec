package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.SpecialItemPlugin.SpecialItemRendererAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.dialog.VT_ChestRenameDialog;
import de.schafunschaf.voidtec.campaign.listeners.VT_BaseChestStorageListener;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@Setter
public class BaseChestData extends SpecialItemData implements StorageChestData {

    protected String name;
    protected String manufacturer;
    protected final CargoAPI chestStorage;
    protected final int maxSize;
    protected int currentSize;
    protected String baseSprite = VT_Icons.AUGMENT_ITEM_CHEST_ICON;
    protected String glowSprite = VT_Icons.AUGMENT_ITEM_CHEST_ICON_GLOW;
    protected UseMode currentMode;

    public BaseChestData(String id, String data, String name, String manufacturer, int maxSize,
                         CargoAPI chestInventory, boolean isLocked) {
        super(id, data);
        if (isNull(data)) {
            setData(Misc.genUID());
        }
        this.name = name;
        this.manufacturer = manufacturer;
        this.chestStorage = isNull(chestInventory) ? Global.getFactory().createCargo(true) : chestInventory;
        this.maxSize = maxSize;
        this.currentMode = isLocked ? UseMode.LOCKED : UseMode.OPEN;
    }

    public BaseChestData(String id, String data, String name, String manufacturer, int maxSize, CargoAPI chestInventory, String baseSprite,
                         String glowSprite, boolean isLocked) {
        super(id, data);
        this.name = name;
        this.manufacturer = manufacturer;
        this.chestStorage = isNull(chestInventory) ? Global.getFactory().createCargo(true) : chestInventory;
        this.maxSize = maxSize;
        this.baseSprite = baseSprite;
        this.glowSprite = glowSprite;
        this.currentMode = isLocked ? UseMode.LOCKED : UseMode.OPEN;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chestStorage == null) ? 0 : chestStorage.hashCode());
        return result;
    }

    @Getter
    @RequiredArgsConstructor
    public enum UseMode {
        OPEN(Misc.getPositiveHighlightColor()),
        RENAME(Misc.getHighlightColor()),
        LOCKED(Misc.getNegativeHighlightColor());

        private final Color color;
        public static final UseMode[] values = values();

        public UseMode cycleNextMode() {
            if (this != LOCKED) {
                return this == OPEN ? RENAME : OPEN;
            }

            return this;
        }
    }

    @Override
    public String getTitle() {
        return "Store Items";
    }

    @Override
    public CargoAPI getAllowedCargo() {
        return CargoUtils.getPlayerCargoForChestStorage();
    }

    @Override
    public Color getColor() {
        return VoidTecUtils.getManufacturerColor(manufacturer);
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;

        tooltip.addSpacer(largePad);
        addDescription(tooltip);
    }

    protected void addDescription(TooltipMakerAPI tooltip) {
        String hlString = maxSize + " Items";
        tooltip.addPara(String.format(
                "This handy little storage unit was designed for any eager captain or collector in the sector and stores up to %s inside.",
                hlString), 0f, Misc.getHighlightColor(), hlString);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        SpriteAPI base = Global.getSettings().getSprite(baseSprite);

        base.renderAtCenter(0f, 0f);

        if (!isNull(glowSprite)) {
            SpriteAPI glow = Global.getSettings().getSprite(glowSprite);

            glow.setColor(Misc.scaleAlpha(getColor(), 0.5f));
            glow.renderAtCenter(0f, 0f);
        }

        FactionAPI faction = Global.getSector().getFaction(manufacturer);
        if (!isNull(faction)) {
            SpriteAPI flag = Global.getSettings().getSprite(faction.getCrest());

            float tlX = -26f;
            float tlY = 12f;
            float blX = -33f;
            float blY = -3f;
            float trX = -7f;
            float trY = 5f;
            float brX = -13f;
            float brY = -12f;

            if (VT_Settings.alternativeChestFlagDisplay) {
                blX = 4f;
                blY = -18f;
                tlX = 4f;
                tlY = 0f;
                trX = 28f;
                trY = 14f;
                brX = 29f;
                brY = -3f;

                flag = Global.getSettings().getSprite(faction.getLogo());
            }

            flag.setAlphaMult(0.8f);
            flag.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            renderer.renderScanlinesWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY, 2, true);
        }
    }

    @Override
    public void openDialog(InteractionDialogAPI dialog, StorageChestPlugin storageChestPlugin) {
        dialog.getTextPanel().addPara(VT_Strings.VT_SHEEP_WIKI);
        switch (currentMode) {
            case OPEN:
                CargoAPI allowedCargo = getAllowedCargo();
                dialog.showCargoPickerDialog(getTitle(), "Store", "Cancel", false, 110f, allowedCargo,
                                             new VT_BaseChestStorageListener(storageChestPlugin, allowedCargo, dialog));
                break;
            case RENAME:
                CustomDialogDelegate delegate = new VT_ChestRenameDialog(dialog, storageChestPlugin);
                dialog.showCustomDialog(310, 70, delegate);
                break;
            case LOCKED:
        }
    }

    @Override
    public List<String> getAllowedItemsString() {
        return Collections.singletonList("All");
    }
}



























