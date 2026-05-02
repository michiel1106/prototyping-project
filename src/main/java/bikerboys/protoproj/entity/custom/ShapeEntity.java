package bikerboys.protoproj.entity.custom;

import bikerboys.protoproj.entity.ModEntities;
import bikerboys.protoproj.translation.TranslationCube;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShapeEntity extends Mob {

    private final List<TranslationCube> cubes = new ArrayList<>();
    private final List<Integer> colors = new ArrayList<>();

    public ShapeEntity(EntityType<? extends ShapeEntity> type, Level level) {
        super(type, level);
    }

    public ShapeEntity(Level level) {
        super(ModEntities.SHAPE_ENTITY, level);
    }

    public static AttributeSupplier.Builder createShapeAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    // ---------------- CUBES ----------------

    public List<TranslationCube> getCubes() {
        return cubes;
    }

    public void addCube(TranslationCube cube, int color) {
        cubes.add(cube);
        colors.add(color);
    }

    public void setCube(int index, TranslationCube cube) {
        if (index >= 0 && index < cubes.size()) {
            cubes.set(index, cube);
        }
    }

    public TranslationCube getCube(int index) {
        return cubes.get(index);
    }

    public void removeCube(int index) {
        if (index >= 0 && index < cubes.size()) {
            cubes.remove(index);
            colors.remove(index);
        }
    }

    // ---------------- COLORS ----------------

    public List<Integer> getColors() {
        return colors;
    }

    public int getColor(int index) {
        if (index >= 0 && index < colors.size()) {
            return colors.get(index);
        }
        return 0xFFFFFFFF;
    }

    public void setColor(int index, int color) {
        if (index >= 0 && index < colors.size()) {
            colors.set(index, color);
        }
    }
}