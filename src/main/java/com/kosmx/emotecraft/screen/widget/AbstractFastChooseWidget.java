package com.kosmx.emotecraft.screen.widget;

import com.kosmx.emotecraft.Main;
import com.kosmx.emotecraft.config.EmoteHolder;
import com.kosmx.emotecraft.math.Helper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public abstract class AbstractFastChooseWidget extends DrawableHelper implements Drawable, Element {

    public static final EmoteHolder[] emotes = new EmoteHolder[8];

    public int x;
    public int y;
    protected int size;
    protected final FastChooseElement[] elements = new FastChooseElement[8];
    private boolean hovered;
    private final Identifier TEXTURE = Main.config.dark ? new Identifier(Main.MOD_ID, "textures/gui/fastchoose_dark.png") : new Identifier(Main.MOD_ID, "textures/gui/fastchoose_light.png");

    private AbstractFastChooseWidget(){
        elements[0] = new FastChooseElement(0, 22.5f);
        elements[1] = new FastChooseElement(1, 67.5f);
        elements[2] = new FastChooseElement(2, 157.5f);
        elements[3] = new FastChooseElement(3, 112.5f);
        elements[4] = new FastChooseElement(4, 337.5f);
        elements[5] = new FastChooseElement(5, 292.5f);
        elements[6] = new FastChooseElement(6, 202.5f);
        elements[7] = new FastChooseElement(7, 247.5f);
    }

    public AbstractFastChooseWidget(int x, int y, int size){
        this();
        this.x = x;
        this.y = y;
        this.size = size;       //It's a square with same width and height
    }

    public void drawCenteredText(MatrixStack matrixStack, TextRenderer textRenderer, StringRenderable stringRenderable, float deg){
        drawCenteredText(matrixStack, textRenderer, stringRenderable, (float) (((float)(this.x + this.size/2))*Math.sin(deg * 0.0174533)), (float) (((float)(this.y + this.size/2))*Math.cos(deg * 0.0174533)));
    }

    public static void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, StringRenderable stringRenderable, float x, float y){
        textRenderer.drawWithShadow(matrices, stringRenderable, x - (float)textRenderer.getWidth(stringRenderable) / 2, y - 2, Helper.colorHelper(1, 1, 1, 1));
    }

    @Nullable
    protected FastChooseElement getActivePart(int mouseX, int mouseY){
        int x = mouseX - this.x - this.size/2;
        int y = mouseY - this.y - this.size/2;
        int i = 0;
        if(x == 0){
            return null;
        }
        else if(x < 0){
            i += 4;
        }

        if(y == 0){
            return null;
        }
        else if (y < 0){
            i += 2;
        }

        if (Math.abs(x) == Math.abs(y)){
            return null;
        }
        else if(Math.abs(x) > Math.abs(y)){
            i++;
        }
        return elements[i];
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        checkHovered(mouseX, mouseY);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        minecraftClient.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, 0, 0, 0, 0, 256);
        for(FastChooseElement f:elements){
            if(f.hasEmote()) f.renderText(matrices, textRenderer);
        }
        if(this.hovered){
            FastChooseElement part = getActivePart(mouseX, mouseY);
            if(part != null && part.hasEmote()){
                part.renderHover(matrices);
            }
        }
    }

    /**
     * @param matrices MatrixStack ...
     * @param x Render x from this pixel
     * @param y same
     * @param u texture x
     * @param v texture y
     * @param s used texture part size !NOT THE WHOLE TEXTURE IMAGE SIZE!
     */
    private void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int s){
        drawTexture(matrices, this.x + x, this.y + y, this.size, this.size, u, v, s, s, 512, 512);
    }

    private void checkHovered(int mouseX, int mouseY){
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX <= this.x + this.size && mouseY <= this.y + this.size;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        checkHovered((int)mouseX, (int)mouseY);
        if(this.isValidClickButton(button)){
            FastChooseElement element = this.getActivePart((int)mouseX, (int)mouseY);
            if(element != null){
                return onClick(element, button);
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        this.checkHovered((int)mouseX, (int)mouseY);
        return this.hovered;
    }

    protected abstract boolean isValidClickButton(int button);
    protected abstract boolean onClick(FastChooseElement element, int button);  //What DO I want to do with this element? set or play.

    protected class FastChooseElement{
        private final float angle;
        private final int id;
        @Nullable

        protected FastChooseElement(int num, float angle){
            this.angle = angle;
            this.id = num;
        }

        public boolean hasEmote(){
            return AbstractFastChooseWidget.emotes[id] != null;
        }

        public void setEmote(@Nullable EmoteHolder emote){
            emotes[id] = emote;
        }

        @Nullable
        public EmoteHolder getEmote(){
            return emotes[id];
        }

        public void clearEmote(){
            this.setEmote(null);
        }

        public void renderText(MatrixStack matrices, TextRenderer textRenderer) {
            if (emotes[id] != null) {
                drawCenteredText(matrices, textRenderer, emotes[id].name, this.angle);
            }
            else {
                Main.log(Level.ERROR, "Tried to render non-existing name", true);
            }
        }
        public void renderHover(MatrixStack matrices){
            int textX = 0;
            int textY = 0;
            int x = 0;
            int y = 0;

            if((id & 1) == 0){
                textX = 256;
            }
            else {
                textY = 256;
            }

            if((id & 2) == 2){
                y = 128;
            }

            if((id & 4) == 4){
                x = 128;
            }
            drawTexture(matrices, x, y, textX + x, textY + y, 128);
        }
    }
}