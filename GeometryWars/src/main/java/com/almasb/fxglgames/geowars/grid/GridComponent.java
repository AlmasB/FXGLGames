package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GridComponent extends Component {

    private Array<Line> lines = new Array<>(1000);
    private Array<ExtraLine> extraLines = new Array<>(1000);

    private GraphicsContext g;

    public GridComponent(GraphicsContext g) {
        this.g = g;
    }

    @Override
    public void onUpdate(double tpf) {
        for (Line line: lines)
            line.update(g);

        for (ExtraLine line: extraLines)
            line.update(g);
    }

    public void addLine(PointMass end1, PointMass end2) {
        lines.add(new Line(end1, end2));
    }

    public void addExtraLine(PointMass end11, PointMass end12, PointMass end21, PointMass end22) {
        extraLines.add(new ExtraLine(end11, end12, end21, end22));
    }

    private static class Line {
        private PointMass end1, end2;

        Line(PointMass end1, PointMass end2) {
            this.end1 = end1;
            this.end2 = end2;
        }

        void update(GraphicsContext g) {
            g.strokeLine(end1.getPosition().x, end1.getPosition().y, end2.getPosition().x, end2.getPosition().y);
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

        void update(GraphicsContext g) {
            position1.x = end11.getPosition().x + (end12.getPosition().x - end11.getPosition().x) / 2;
            position1.y = end11.getPosition().y + (end12.getPosition().y - end11.getPosition().y) / 2;

            position2.x = end21.getPosition().x + (end22.getPosition().x - end21.getPosition().x) / 2;
            position2.y = end21.getPosition().y + (end22.getPosition().y - end21.getPosition().y) / 2;

            g.strokeLine(position1.x, position1.y, position2.x, position2.y);
        }
    }
}
