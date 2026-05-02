package bikerboys.protoproj.entity;

import bikerboys.protoproj.*;
import bikerboys.protoproj.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;

public class ModEntities {


    public static final EntityType<ShapeEntity> SHAPE_ENTITY = register(
            "shape_entity",
            EntityType.Builder.<ShapeEntity>of((ShapeEntity::new), MobCategory.MISC)
                    .sized(0.75f, 0.75f)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(PrototypingProject.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }


    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(SHAPE_ENTITY, ShapeEntity.createShapeAttributes());
    }

    public static void init() {
        registerAttributes();

    }

}
