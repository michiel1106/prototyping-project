package bikerboys.protoproj.client.rendering.entity;


import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.*;

import java.util.*;

public class ShapeEntityRenderState extends LivingEntityRenderState {
    CubeListBuilder cubeListBuilder = new CubeListBuilder();
    List<Integer> colorOffsets = new ArrayList<>();

}
