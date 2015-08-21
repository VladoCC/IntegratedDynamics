package org.cyclops.integrateddynamics.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.cyclops.cyclopscore.client.gui.image.Images;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.StringHelpers;
import org.cyclops.cyclopscore.inventory.IGuiContainerProvider;
import org.cyclops.integrateddynamics.core.client.gui.container.GuiMultipartAspects;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import org.cyclops.integrateddynamics.core.part.IPartContainer;
import org.cyclops.integrateddynamics.core.part.PartTarget;
import org.cyclops.integrateddynamics.core.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.inventory.container.ContainerPartWriter;
import org.cyclops.integrateddynamics.item.ItemVariable;

import java.awt.*;
import java.util.List;


/**
 * Gui for a writer part.
 * @author rubensworks
 */
public class GuiPartWriter<P extends IPartTypeWriter<P, S> & IGuiContainerProvider, S extends IPartStateWriter<P>>
        extends GuiMultipartAspects<P, S, IAspectWrite> {

    private static final int ERROR_X = 152;
    private static final int ERROR_Y = 20;
    private static final int OK_X = 152;
    private static final int OK_Y = 20;

    /**
     * Make a new instance.
     * @param partTarget The target.
     * @param player The player.
     * @param partContainer The part container.
     * @param partType The targeted part type.
     */
    public GuiPartWriter(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer, P partType) {
        super(new ContainerPartWriter<P, S>(player, partTarget, partContainer, partType));
    }

    @Override
    protected String getNameId() {
        return "partWriter";
    }

    @Override
    protected void drawAdditionalElementInfoForeground(ContainerMultipartAspects<P, S, IAspectWrite> container, int index, IAspectWrite aspect, int mouseX, int mouseY) {
        // Render error tooltip
        List<L10NHelpers.UnlocalizedString> errors = getPartState().getErrors(aspect);
        if(!errors.isEmpty()) {
            if(isPointInRegion(ERROR_X, ERROR_Y + index * container.getAspectBoxHeight(), Images.ERROR.getSheetWidth(), Images.ERROR.getSheetHeight(), mouseX, mouseY)) {
                List<String> lines = Lists.newLinkedList();
                for(L10NHelpers.UnlocalizedString error : errors) {
                    lines.addAll(StringHelpers.splitLines(error.localize(), L10NHelpers.MAX_TOOLTIP_LINE_LENGTH,
                            EnumChatFormatting.RED.toString()));
                }
                drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }

    @Override
    protected void drawAdditionalElementInfo(ContainerMultipartAspects container, int index, IAspectWrite aspect) {
        int aspectBoxHeight = container.getAspectBoxHeight();

        // Render dummy target item
        // This could be cached if this would prove to be a bottleneck
        ItemStack itemStack = container.writeAspectInfo(false, new ItemStack(ItemVariable.getInstance()), aspect);
        Rectangle pos = getElementPosition(container, index, true);
        itemRender.renderItemAndEffectIntoGUI(itemStack, pos.x, pos.y);

        // Render error symbol
        mc.renderEngine.bindTexture(texture);
        if(!getPartState().getErrors(aspect).isEmpty()) {
            Images.ERROR.draw(this, guiLeft + offsetX + ERROR_X,
                    guiTop + offsetY + ERROR_Y + aspectBoxHeight * index);
        } else if(getPartState().getActiveAspect() == aspect) {
            Images.OK.draw(this, guiLeft + offsetX + OK_X,
                    guiTop + offsetY + OK_Y + aspectBoxHeight * index);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        ContainerPartWriter container = (ContainerPartWriter) getContainer();
        fontRendererObj.drawString(container.getWriteValue(), this.guiLeft + offsetX + 53,
                this.guiTop + offsetY + 128, container.getWriteValueColor());
    }

    @Override
    protected int getBaseXSize() {
        return 195;
    }

    @Override
    protected int getBaseYSize() {
        return 222;
    }
}
