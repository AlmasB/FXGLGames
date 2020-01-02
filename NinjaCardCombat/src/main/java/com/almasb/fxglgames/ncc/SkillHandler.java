package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.entity.Entity;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@FunctionalInterface
public interface SkillHandler {

    void onSkill(Entity user, Skill skill, List<Entity> targets);
}
