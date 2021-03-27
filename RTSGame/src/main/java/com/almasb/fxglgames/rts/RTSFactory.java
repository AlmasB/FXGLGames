package com.almasb.fxglgames.rts;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class RTSFactory implements EntityFactory {

    @Spawns("unit")
    public Entity newUnit(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(40, 40))
                .build();
    }

    @Spawns("worker")
    public Entity newWorker(SpawnData data) {
        var e = entityBuilder(data)
                .type(EntityType.WORKER)
                .view(new Rectangle(40, 40, Color.BLUE))
                .with(ResourceType.WOOD.toString(), 0)
                .with(ResourceType.STONE.toString(), 0)
                .with(new StateComponent())
                .with(new ActionComponent())
                .with(new GathererComponent())
                .build();

        e.getProperties().intProperty(ResourceType.WOOD.toString()).addListener((observable, oldValue, newValue) -> {
            System.out.println("WOOD: " + newValue);
        });

        e.getProperties().intProperty(ResourceType.STONE.toString()).addListener((observable, oldValue, newValue) -> {
            System.out.println("STONE: " + newValue);
        });

        var text = FXGL.getUIFactoryService().newText(e.getProperties().intProperty("WOOD").asString("Wood: %d"));
        text.setFill(Color.BLACK);

        var text2 = FXGL.getUIFactoryService().newText(e.getProperties().intProperty("STONE").asString("Stone: %d"));
        text2.setTranslateY(-20);
        text2.setFill(Color.BLACK);

        e.getViewComponent().addChild(text);
        e.getViewComponent().addChild(text2);

        return e;
    }

    @Spawns("stockpile")
    public Entity newStockpile(SpawnData data) {
        var view = new Rectangle(200, 200, null);
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(2.5);

        var e = entityBuilder(data)
                .type(EntityType.STOCKPILE)
                .viewWithBBox(view)
                .with(ResourceType.WOOD.toString(), 0)
                .with(ResourceType.STONE.toString(), 0)
                .build();

        var text = FXGL.getUIFactoryService().newText(e.getProperties().intProperty("WOOD").asString("Wood: %d"));
        text.setFill(Color.BLACK);

        var text2 = FXGL.getUIFactoryService().newText(e.getProperties().intProperty("STONE").asString("Stone: %d"));
        text2.setTranslateY(-20);
        text2.setFill(Color.BLACK);

        e.getViewComponent().addChild(text);
        e.getViewComponent().addChild(text2);

        return e;
    }

    @Spawns("tree")
    public Entity newTree(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.TREE)
                .viewWithBBox("tree.png")
                .with(new ResourceComponent(ResourceType.WOOD, 100))
                .build();
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.STONE)
                .viewWithBBox("stone.png")
                .with(new ResourceComponent(ResourceType.STONE, 50))
                .build();
    }
}
