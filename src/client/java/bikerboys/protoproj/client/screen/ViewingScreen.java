package bikerboys.protoproj.client.screen;

import bikerboys.protoproj.client.rendering.*;
import bikerboys.protoproj.entity.custom.*;
import bikerboys.protoproj.translation.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
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
import java.util.function.Consumer;

public class ViewingScreen extends Screen {


    public ViewingScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        super.init();

    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Matrix3x2fStack pose = graphics.pose();
        pose.popMatrix();

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(786432));

        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.SOLID_COLOR_CUBE);




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