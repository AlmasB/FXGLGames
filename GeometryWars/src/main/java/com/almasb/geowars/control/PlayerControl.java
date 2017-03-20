package com.almasb.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private GameEntity player;
    private long spawnTime = System.currentTimeMillis();

    @Override
    public void onAdded(Entity entity) {
        player = (GameEntity) entity;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void left() {
        player.translateX(-5);
        makeExhaustFire();
    }

    public void right() {
        player.translateX(5);
        makeExhaustFire();
    }

    public void up() {
        player.translateY(-5);
        makeExhaustFire();
    }

    public void down() {
        player.translateY(5);
        makeExhaustFire();
    }

    private void makeExhaustFire() {
        Vec2 position = new Vec2(player.getCenter().getX(), player.getCenter().getY());
        double rotation = player.getRotation();

        Color midColor = Color.color(1f, 0.73f, 0.12f, 0.7f);
        Color sideColor = Color.color(0.78f, 0.15f, 0.04f, 0.7f);

        Vec2 direction = Vec2.fromAngle(rotation);

        float t = (System.currentTimeMillis() - spawnTime) / 1000f;

        Vec2 baseVel = direction.mul(-45f);
        Vec2 perpVel = new Vec2(baseVel.y, -baseVel.x).mulLocal(2f * FXGLMath.sin(t * 10f));



        // subtract half extent x, y of Glow.png                                                 mul half extent player
        Vec2 pos = position.sub(new Vec2(17.5, 10)).addLocal(direction.negate().normalizeLocal().mulLocal(20));

        // middle stream
        Vec2 randVec = Vec2.fromAngle(FXGLMath.radiansToDegrees * FXGLMath.random() * FXGLMath.PI2);
        Vec2 velMid = baseVel.add(randVec.mul(7.5f));

        Entities.builder()
                .at(pos.x, pos.y)
                //.at(position.x - 17.5, position.y - 10)
                .viewFromTexture("Glow.png")
                .with(new ParticleControl(new Point2D(velMid.x, velMid.y), 800, midColor))
                .buildAndAttach(FXGL.getApp().getGameWorld());

//        Spatial particleMid = standardParticle.clone();
//        particleMid.setLocalTranslation(pos);
//        particleMid.addControl(new ParticleControl(velMid, 800, midColor,
//                screenWidth, screenHeight));
//        particleMid.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleMid);
//
//        Spatial particleMidGlow = glowParticle.clone();
//        particleMidGlow.setLocalTranslation(pos);
//        particleMidGlow.addControl(new ParticleControl(velMid, 800, midColor,
//                screenWidth, screenHeight));
//        particleMidGlow.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleMidGlow);

        // side streams
//        Vector3f randVec1 = MonkeyBlasterMain.getVectorFromAngle(new Random()
//                .nextFloat() * FastMath.PI * 2);
//        Vector3f randVec2 = MonkeyBlasterMain.getVectorFromAngle(new Random()
//                .nextFloat() * FastMath.PI * 2);
//        Vector3f velSide1 = baseVel.add(randVec1.mult(2.4f)).addLocal(perpVel);
//        Vector3f velSide2 = baseVel.add(randVec2.mult(2.4f)).subtractLocal(
//                perpVel);
//
//        Spatial particleSide1 = standardParticle.clone();
//        particleSide1.setLocalTranslation(pos);
//        particleSide1.addControl(new ParticleControl(velSide1, 800, sideColor,
//                screenWidth, screenHeight));
//        particleSide1.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleSide1);
//
//        Spatial particleSide2 = standardParticle.clone();
//        particleSide2.setLocalTranslation(pos);
//        particleSide2.addControl(new ParticleControl(velSide2, 800, sideColor,
//                screenWidth, screenHeight));
//        particleSide2.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleSide2);
//
//        Spatial particleSide1Glow = glowParticle.clone();
//        particleSide1Glow.setLocalTranslation(pos);
//        particleSide1Glow.addControl(new ParticleControl(velSide1, 800,
//                sideColor, screenWidth, screenHeight));
//        particleSide1Glow.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleSide1Glow);
//
//        Spatial particleSide2Glow = glowParticle.clone();
//        particleSide2Glow.setLocalTranslation(pos);
//        particleSide2Glow.addControl(new ParticleControl(velSide2, 800,
//                sideColor, screenWidth, screenHeight));
//        particleSide2Glow.setUserData("affectedByGravity", true);
//        ((Node) guiNode.getChild("particles")).attachChild(particleSide2Glow);
    }
}
