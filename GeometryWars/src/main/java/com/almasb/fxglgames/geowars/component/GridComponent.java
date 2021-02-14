package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.BULLET;

/**
 * Adapted from https://gamedevelopment.tutsplus.com/tutorials/make-a-neon-vector-shooter-with-jme-warping-grid--gamedev-12413
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GridComponent extends Component {

    private static final double POINT_MASS_DAMPING = 0.8;
    private static final double SPRING_STIFFNESS = 0.28;
    private static final double SPRING_DAMPING = 0.86;

    private static final Color IDLE_COLOR = Color.color(0.138, 0.138, 0.375, 0.66);
    private static final Color EDGE_COLOR = IDLE_COLOR.brighter().brighter();
    private static final Color BULLET_COLOR = Color.color(0.138, 0.238, 0.975, 0.76);

    private Array<Line> lines = new Array<>(1000);
    private Array<ExtraLine> extraLines = new Array<>(1000);

    private List<Spring> springs = new ArrayList<>();
    private PointMass[][] points;

    private Canvas onScreenCanvas = new Canvas(getAppWidth(), getAppHeight());
    private Canvas offScreenCanvas = new Canvas(getAppWidth(), getAppHeight());

    private GridRenderThread gridRenderThread = new GridRenderThread();

    private List<Entity> bullets = new ArrayList<>();

    public GridComponent() {
        Point2D spacing = new Point2D(38.8 / 2.0, 40 / 2.0);

        int numColumns = (int) (getAppWidth() / spacing.getX()) + 2;
        int numRows = (int) (getAppHeight() / spacing.getY()) + 2;
        points = new PointMass[numColumns][numRows];

        PointMass[][] fixedPoints = new PointMass[numColumns][numRows];

        // create the point masses
        float xCoord = 0, yCoord = 0;
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                points[column][row] = new PointMass(new Vec2(xCoord, yCoord), POINT_MASS_DAMPING, 1);
                fixedPoints[column][row] = new PointMass(new Vec2(xCoord, yCoord), POINT_MASS_DAMPING, 0);
                xCoord += spacing.getX();
            }
            yCoord += spacing.getY();
            xCoord = 0;
        }

        // link the point masses with springs
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (x == 0 || y == 0 || x == numColumns - 1 || y == numRows - 1) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.5, 0.1));
                } else if (x % 3 == 0 && y % 3 == 0) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.005, 0.02));
                }

                if (x > 0) {
                    springs.add(new Spring(points[x - 1][y], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING));

                    addLine(points[x - 1][y], points[x][y]);
                }

                if (y > 0) {
                    springs.add(new Spring(points[x][y - 1], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING));

                    addLine(points[x][y - 1], points[x][y]);
                }

                // add additional lines
                if (x > 0 && y > 0) {
                    addExtraLine(
                            points[x - 1][y], points[x][y],
                            points[x - 1][y - 1], points[x][y - 1]);

                    addExtraLine(
                            points[x][y - 1], points[x][y],
                            points[x - 1][y - 1], points[x - 1][y]);
                }
            }
        }
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(onScreenCanvas);

        gridRenderThread.start();
    }

    @Override
    public void onUpdate(double tpf) {
        if (gridRenderThread.isRenderDone.get()) {
            bullets = byType(BULLET);

            gridRenderThread.isRenderDone.set(false);
        }
    }

    private class GridRenderThread extends Thread {

        AtomicBoolean isRenderDone = new AtomicBoolean(false);

        GridRenderThread() {
            super("GridRenderThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                if (isRenderDone.get()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO:
                        e.printStackTrace();
                    }

                    continue;
                }

                springs.forEach(Spring::update);

                for (int x = 0; x < points.length; x++) {
                    for (int y = 0; y < points[0].length; y++) {
                        points[x][y].update();
                    }
                }

                // render
                offScreenCanvas.getGraphicsContext2D().clearRect(0, 0, getAppWidth(), getAppHeight());

                for (Line line : lines)
                    line.render(offScreenCanvas.getGraphicsContext2D());

                for (ExtraLine line : extraLines)
                    line.render(bullets, offScreenCanvas.getGraphicsContext2D());

                var g = offScreenCanvas.getGraphicsContext2D();

                g.setStroke(EDGE_COLOR);
                g.setLineWidth(7.5);
                g.strokeLine(0, 0, 0, getAppHeight());
                g.strokeLine(0, 0, getAppWidth(), 0);
                g.strokeLine(getAppWidth(), 0, getAppWidth(), getAppHeight());
                g.strokeLine(0, getAppHeight(), getAppWidth(), getAppHeight());
                g.setStroke(IDLE_COLOR);
                g.setLineWidth(1);

                isRenderDone.set(true);

                Platform.runLater(() -> {
                    entity.getViewComponent().removeChild(onScreenCanvas);

                    var tmp = onScreenCanvas;
                    onScreenCanvas = offScreenCanvas;
                    offScreenCanvas = tmp;

                    entity.getViewComponent().addChild(onScreenCanvas);
                });
            }
        }
    }

    public void addLine(PointMass end1, PointMass end2) {
        lines.add(new Line(end1, end2));
    }

    public void addExtraLine(PointMass end11, PointMass end12, PointMass end21, PointMass end22) {
        extraLines.add(new ExtraLine(end11, end12, end21, end22));
    }

    public void applyExplosiveForce(double force, Point2D position, double radius) {
        Vec2 tmpVec = new Vec2();

        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                double dist = position.distance(points[x][y].getPosition().x, points[x][y].getPosition().y);
                dist *= dist;

                if (dist < radius * radius) {
                    tmpVec.set((float) position.getX(), (float) position.getY());
                    tmpVec.subLocal(points[x][y].getPosition()).mulLocal((float) (-10f * force / (10000 + dist)));

                    points[x][y].applyForce(tmpVec);
                    points[x][y].increaseDamping(0.6f);
                }
            }
        }
    }

    public void applyImplosiveForce(double force, Point2D position, double radius) {
        Vec2 tmpVec = new Vec2();

        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                double dist = position.distance(points[x][y].getPosition().x, points[x][y].getPosition().y);
                dist *= dist;

                if (dist < radius * radius) {
                    tmpVec.set((float) position.getX(), (float) position.getY());
                    tmpVec.subLocal(points[x][y].getPosition()).mulLocal((float) (10f * force / (10000 + dist)));

                    points[x][y].applyForce(tmpVec);
                    points[x][y].increaseDamping(0.6f);
                }
            }
        }
    }

//    public void applyDirectedForce(Vector3f force, Vector3f position, float radius) {
//        for (int x=0; x<points.length; x++) {
//            for (int y=0; y<points[0].length; y++) {
//                if (position.distanceSquared(points[x][y].getPosition()) < radius * radius) {
//                    float forceFactor = 10 / (10 + position.distance(points[x][y].getPosition()));
//                    points[x][y].applyForce(force.mult(forceFactor));
//                }
//            }
//        }
//    }

    private static class Line {
        private PointMass end1, end2;

        Line(PointMass end1, PointMass end2) {
            this.end1 = end1;
            this.end2 = end2;
        }

        void render(GraphicsContext g) {
            g.strokeLine(end1.getPosition().x, end1.getPosition().y, end2.getPosition().x, end2.getPosition().y);

            g.setStroke(IDLE_COLOR);
        }
    }

    private static class ExtraLine {

        private PointMass end11, end12, end21, end22;

        private Vec2 position1 = new Vec2();
        private Vec2 position2 = new Vec2();

        ExtraLine(PointMass end11, PointMass end12, PointMass end21, PointMass end22) {
            this.end11 = end11;
            this.end12 = end12;
            this.end21 = end21;
            this.end22 = end22;
        }

        void render(List<Entity> bullets, GraphicsContext g) {
            position1.x = end11.getPosition().x + (end12.getPosition().x - end11.getPosition().x) / 2;
            position1.y = end11.getPosition().y + (end12.getPosition().y - end11.getPosition().y) / 2;

            position2.x = end21.getPosition().x + (end22.getPosition().x - end21.getPosition().x) / 2;
            position2.y = end21.getPosition().y + (end22.getPosition().y - end21.getPosition().y) / 2;

            if (
                    bullets.stream()
                            .anyMatch(e -> {
                                var distance = e.getPosition().distance(position1.toPoint2D());
                                return distance > 15 && distance < 70;
                            })
            ) {
                g.setStroke(BULLET_COLOR);
            }

            g.strokeLine(position1.x, position1.y, position2.x, position2.y);

            g.setStroke(IDLE_COLOR);
        }
    }

    private static class PointMass {

        private Vec2 position;
        private Vec2 velocity = new Vec2();
        private Vec2 acceleration = new Vec2();

        private final float initialDamping;
        private float damping;
        private float inverseMass;

        public PointMass(Vec2 position, double damping, double inverseMass) {
            this.position = position;
            this.damping = (float) damping;
            this.initialDamping = (float) damping;
            this.inverseMass = (float) inverseMass;
        }

        public void applyForce(Vec2 force) {
            acceleration.addLocal(force.mul(inverseMass));
        }

        public void increaseDamping(double factor) {
            damping *= factor;
        }

        public void update() {
            applyAcceleration();
            applyVelocity();

            damping = initialDamping;
        }

        public Vec2 getPosition() {
            return position;
        }

        public Vec2 getVelocity() {
            return velocity;
        }

        private void applyAcceleration() {
            velocity = velocity.add(acceleration);
            acceleration.setZero();
        }

        private void applyVelocity() {
            position.addLocal(velocity.mul(0.6f));

            if (velocity.lengthSquared() < 0.0001) {
                velocity.setZero();
            }

            velocity.mulLocal(damping);
        }
    }

    private static class Spring {
        private final PointMass end1;
        private final PointMass end2;

        private final double lengthAtRest;

        private final float stiffness;
        private final float damping;

        public Spring(PointMass end1, PointMass end2, double stiffness, double damping) {
            this.end1 = end1;
            this.end2 = end2;
            this.stiffness = (float) stiffness;
            this.damping = (float) damping / 10;
            lengthAtRest = end1.getPosition().distance(end2.getPosition().x, end2.getPosition().y) * 0.95f;
        }

        public void update() {
            Vec2 current = new Vec2()
                    .set(end1.getPosition())
                    .subLocal(end2.getPosition());

            float currentLength = current.length();

            if (currentLength > lengthAtRest) {
                Vec2 dv = new Vec2()
                        .set(end2.getVelocity())
                        .subLocal(end1.getVelocity())
                        .mulLocal(damping);

                Vec2 force = current.normalizeLocal()
                        .mulLocal(currentLength - lengthAtRest)
                        .mulLocal(stiffness)
                        .subLocal(dv);

                end2.applyForce(force);
                end1.applyForce(force.negateLocal());
            }
        }
    }

    private static boolean circleIntersectsLine(double r, Point2D circleCenter, Point2D lineStart, Point2D lineEnd) {
        var d = lineEnd.subtract(lineStart);
        var f = lineStart.subtract(circleCenter);


        var a = d.dotProduct(d);
        var b = 2 * f.dotProduct(d);
        var c = f.dotProduct(f) - r * r;

        var discriminant = b * b - 4f * a * c;
        if (discriminant < 0) {
            // no intersection
            return false;
        } else {
            // ray didn't totally miss sphere,
            // so there is a solution to
            // the equation.

            discriminant = Math.sqrt(discriminant);

            // either solution may be on or off the ray so need to test both
            // t1 is always the smaller varue, because BOTH discriminant and
            // a are nonnegative.
            var t1 = (-b - discriminant) / (2 * a);
            var t2 = (-b + discriminant) / (2 * a);

            // 3x HIT cases:
            //          -o->             --|-->  |            |  --|->
            // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

            // 3x MISS cases:
            //       ->  o                     o ->              | -> |
            // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

            if (t1 >= 0 && t1 <= 1) {
                // t1 is the intersection, and it's closer than t2
                // (since t1 uses -b - discriminant)
                // Impale, Poke
                return true;
            }

            // here t1 didn't intersect so we are either started
            // inside the sphere or completely past it
            if( t2 >= 0 && t2 <= 1 )
            {
                // ExitWound
                return true;
            }

            return false;
        }
    }
}
