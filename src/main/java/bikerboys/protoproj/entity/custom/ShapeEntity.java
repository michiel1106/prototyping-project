package bikerboys.protoproj.entity.custom;

import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.storage.*;

public class ShapeEntity extends Mob {


    public ShapeEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }


    public static AttributeSupplier.Builder createShapeAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.TEMPT_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }
}
