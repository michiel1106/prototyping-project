package bikerboys.protoproj.client.rendering;

import com.mojang.authlib.minecraft.client.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.item.properties.numeric.*;
import net.minecraft.core.*;
import org.joml.*;

import java.util.*;
import java.util.Random;
import java.util.concurrent.*;

public class FakeChunkRendering {



    public static SectionRenderDispatcher.RenderSection getRenderSection(int chunkX, int chunkZ) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        SectionRenderDispatcher sectionRenderDispatcher = minecraft.levelRenderer.getSectionRenderDispatcher();
        if (sectionRenderDispatcher == null) return null;
        if (level == null) {return null;}


        Random rand = new Random();
        int index = rand.nextInt();


        int lowSectionY = level.getMinSectionY();  // usually -4
        int maxSectionY = level.getMaxSectionY();  // usually 19


        for (int sectionY = lowSectionY; sectionY < maxSectionY; sectionY++) {
            // Convert section coordinates to the long format
            long sectionNode = SectionPos.asLong(chunkX, sectionY, chunkZ);



            // Create the RenderSection

            SectionRenderDispatcher.RenderSection section = sectionRenderDispatcher.new RenderSection(index, sectionNode);



        }




        return null;
    }
}
