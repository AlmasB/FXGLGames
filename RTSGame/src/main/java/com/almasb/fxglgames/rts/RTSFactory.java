package com.almasb.fxglgames.rts;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.action.ActionComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class RTSFactory implements EntityFactory {

    @Spawns("unit")
    public Entity newUnit(SpawnData data) {
        return entityBuilder()
                .from(data)
                .view(new Rectangle(40, 40))
                .build();
    }

    @Spawns("worker")
    public Entity newWorker(SpawnData data) {
        var e = entityBuilder()
                .from(data)
                .type(EntityType.WORKER)
                .view(new Rectangle(40, 40, Color.BLUE))
                .with(ResourceType.WOOD.toString(), 0)
                .with(ResourceType.STONE.toString(), 0)
                .with(new StateComponent())
                .with(new GathererComponent())
                .with(new ActionComponent())
                .build();

        e.getProperties().intProperty(ResourceType.WOOD.toString()).addListener((observable, oldValue, newValue) -> {
            System.out.println("WOOD: " + newValue);
        });

        e.getProperties().intProperty(ResourceType.STONE.toString()).addListener((observable, oldValue, newValue) -> {
            System.out.println("STONE: " + newValue);
        });

//        var text = getUIFactoryService().newText(e.getProperties().intProperty("wood").asString("Carrying wood: %d"));
//        text.setFill(Color.BLACK);
//
//        e.getViewComponent().addChild(text);

        return e;
    }

    @Spawns("stockpile")
    public Entity newStockpile(SpawnData data) {
        var view = new Rectangle(200, 200, null);
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(2.5);

        return entityBuilder()
                .from(data)
                .type(EntityType.STOCKPILE)
                .viewWithBBox(view)
                .build();
    }

    @Spawns("tree")
    public Entity newTree(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(EntityType.TREE)
                .viewWithBBox("tree.png")
                .with(new ResourceComponent(ResourceType.WOOD, 100))
                .build();
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(EntityType.STONE)
                .viewWithBBox("stone.png")
                .with(new ResourceComponent(ResourceType.STONE, 50))
                .build();
    }
}
