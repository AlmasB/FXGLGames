package com.almasb.fxgl.pathfinding;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Pathfinder<T> {

    List<T> findPath(int sourceX, int sourceY, int targetX, int targetY);
}
