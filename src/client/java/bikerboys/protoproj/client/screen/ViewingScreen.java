package bikerboys.protoproj.client.screen;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.advancements.*;
import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.input.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.pig.*;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.*;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.time.*;

public class ViewingScreen extends Screen {

    private int mousebutton = -1;
    private float Msize = 0;
    private float rotation = 0;
    private float yRotation = 0;
    private Vector3f Mtranslation = new Vector3f(0, -0.2f, 0);


    public ViewingScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {

        mousebutton = event.button();
        return super.mouseClicked(event, doubleClick);
    }


    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        mousebutton = -1;

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {

        Msize += (float) (scrollY * 1.4f);

        Msize = Math.clamp(Msize, -40.0f, 80.0f);

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
       // System.out.println("dragged " + dx + " " + dy);
        if (event.input() == 0) {
            rotation += (float) (dx * -1);
            yRotation += (float) ((dy * -1) * 0.01f % Math.PI);
        }

        if (event.input() == 1) {

            Mtranslation = Mtranslation.add((float) dx * 0.01f, (float) dy * 0.01f, 0);

            Mtranslation = new Vector3f(
                    Math.clamp(Mtranslation.x, -3, 3),
                    Math.clamp(Mtranslation.y, -1, 1),
                    Math.clamp(Mtranslation.z, -3, 3)
            );

        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float tickcounter) {
        super.extractBackground(graphics, mouseX, mouseY, tickcounter);

        int x = 300;
        int y = 300;


        if (mousebutton == -1) {
            rotation += tickcounter * 2f; // adjust speed here
            rotation = rotation % 360;
        }

        if (Minecraft.getInstance().level != null) {
            extractEntityInInventoryFollowsMouse(graphics, 0, 0, width, height, 80, 0.0625F, new Pig(EntityType.PIG, Minecraft.getInstance().level), rotation);
        }
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);

    }




    public void extractEntityInInventoryFollowsMouse(
            final GuiGraphicsExtractor graphics,
            final int x0,
            final int y0,
            final int x1,
            final int y1,
            final int size,
            final float offsetY,
            final LivingEntity entity,
            float rot
    ) {

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf xRotation = new Quaternionf().rotationX(yRotation);
        rotation.mul(xRotation);
        EntityRenderState renderState = extractRenderState(entity);
        if (renderState instanceof LivingEntityRenderState livingRenderState) {

            livingRenderState.bodyRot = rot;

            livingRenderState.yRot = 0;



            livingRenderState.xRot = 0;
            livingRenderState.boundingBoxWidth = livingRenderState.boundingBoxWidth / livingRenderState.scale;
            livingRenderState.boundingBoxHeight = livingRenderState.boundingBoxHeight / livingRenderState.scale;
            livingRenderState.scale = 1.0F;
        }

        Vector3f translation = new Vector3f(0F, renderState.boundingBoxHeight / 2.0F + offsetY, 0.0F);


        translation = translation.add(Mtranslation);

        graphics.entity(renderState, size + Msize, translation, rotation, xRotation, x0, y0, x1, y1);
    }

    private static EntityRenderState extractRenderState(final LivingEntity entity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        return renderState;
    }



    @Override
    public boolean isPauseScreen() {
        return false;
    }


    @Override
    public boolean isInGameUi() {
        return true;
    }
}
