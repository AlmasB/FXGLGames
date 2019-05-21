package com.almasb.fxglgames.spacerunner.ai;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum SquadAI {
    INSTANCE;

    private List<Entity> points = new ArrayList<>();
    //private EntityGroup<Entity> enemies;

    public void update(List<Entity> points) {
        this.points.clear();
        this.points.addAll(points);

        //enemies = FXGL.getGameWorld().getGroup(SpaceRunnerType.ENEMY);
    }

    public Entity getBestPoint(Entity enemy) {
        return points.stream()
                .filter(e -> !e.getComponent(AIPointComponent.class).isOccupied() || e.getComponent(AIPointComponent.class).getOccupiedBy() == enemy)
                .min(Comparator.comparing(e -> score(e, enemy))).get();
    }

    public void collisionWithBulletBegin(Entity bullet, Entity point) {
        List<Entity> bullets = point.getObject("collisions");
        bullets.add(bullet);
    }

    public void collisionWithBulletEnd(Entity bullet, Entity point) {
        List<Entity> bullets = point.getObject("collisions");
        bullets.remove(bullet);
    }

    private double score(Entity point, Entity enemy) {
        List<Entity> bullets = point.getObject("collisions");
        bullets.removeIf(e -> !e.isActive());

        int collisions = bullets.size() * 100;

        double distance = enemy.distance(point) / 10;

        point.setProperty("enemies", 0);

//        enemies.forEach(e -> e != enemy, e -> {
//            if (e.distance(point) < 60) {
//                point.setProperty("enemies", point.getInt("enemies") + 1);
//            }
//        });

        int numEnemies = point.getInt("enemies") * 10;

        //System.out.println("For " + point + " have " + collisions + " dist " + distance + " num enemies " + numEnemies);

        double score = collisions + distance + numEnemies;

//        Text text = (Text) point.getView().getNodes().get(0);
//        text.setFill(Color.WHITE);
//        text.setText((int) score + "");

        return score;
    }
}
