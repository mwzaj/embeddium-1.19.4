package me.jellysquid.mods.sodium.client.gui.prompt;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.gui.widgets.AbstractWidget;
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Deprecated(forRemoval = true)
public class ScreenPrompt implements GuiEventListener, Renderable {
    private final ScreenPromptable parent;
    private final List<FormattedText> text;

    private final Action action;

    private FlatButtonWidget closeButton, actionButton;

    private final int width, height;

    public ScreenPrompt(ScreenPromptable parent, List<FormattedText> text, int width, int height, Action action) {
        this.parent = parent;
        this.text = text;

        this.width = width;
        this.height = height;

        this.action = action;
    }

    public void init() {
        var parentDimensions = this.parent.getDimensions();

        int boxX = (parentDimensions.width() / 2) - (width / 2);
        int boxY = (parentDimensions.height() / 2) - (height / 2);

        this.closeButton = new FlatButtonWidget(new Dim2i((boxX + width) - 84, (boxY + height) - 24, 80, 20), Component.literal("Close"), this::close);
        this.closeButton.setStyle(createButtonStyle());

        this.actionButton = new FlatButtonWidget(new Dim2i((boxX + width) - 198, (boxY + height) - 24, 110, 20), this.action.label, this::runAction);
        this.actionButton.setStyle(createButtonStyle());
    }

    public void render(PoseStack drawContext, int mouseX, int mouseY, float delta) {

        drawContext.pushPose();
        drawContext.translate(0.0f, 0.0f, 1000.0f);

        var parentDimensions = this.parent.getDimensions();

        Gui.fill(drawContext, 0, 0, parentDimensions.width(), parentDimensions.height(), 0x70090909);

        drawContext.translate(0.0f, 0.0f, 50.0f);

        int boxX = (parentDimensions.width() / 2) - (width / 2);
        int boxY = (parentDimensions.height() / 2) - (height / 2);

        Gui.fill(drawContext, boxX, boxY, boxX + width, boxY + height, 0xFF171717);
        Gui.renderOutline(drawContext, boxX, boxY, width, height, 0xFF121212);

        drawContext.translate(0.0f, 0.0f, 50.0f);

        int padding = 5;

        int textX = boxX + padding;
        int textY = boxY + padding;

        int textMaxWidth = width - (padding * 2);
        int textMaxHeight = height - (padding * 2);

        var textRenderer = Minecraft.getInstance().font;

        for (var paragraph : this.text) {
            var formatted = textRenderer.split(paragraph, textMaxWidth);

            for (var line : formatted) {
                textRenderer.drawShadow(drawContext, line, textX, textY, 0xFFFFFFFF);
                textY += textRenderer.lineHeight + 2;
            }

            textY += 8;
        }

        for (var button : getWidgets()) {
            button.render(drawContext, mouseX, mouseY, delta);
        }

        drawContext.popPose();
    }

    private static FlatButtonWidget.Style createButtonStyle() {
        var style = new FlatButtonWidget.Style();
        style.bgDefault = 0xff2b2b2b;
        style.bgHovered = 0xff393939;
        style.bgDisabled = style.bgDefault;

        style.textDefault = 0xFFFFFFFF;
        style.textDisabled = style.textDefault;

        return style;
    }

    @NotNull
    public List<AbstractWidget> getWidgets() {
        return List.of(this.actionButton, this.closeButton);
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused) {
            this.parent.setPrompt(this);
        } else {
            this.parent.setPrompt(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : this.getWidgets()) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }

        return GuiEventListener.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isFocused() {
        return this.parent.getPrompt() == this;
    }

    private void close() {
        this.parent.setPrompt(null);
    }

    private void runAction() {
        this.action.runnable.run();
        this.close();
    }

    public record Action(Component label, Runnable runnable) {

    }
}
