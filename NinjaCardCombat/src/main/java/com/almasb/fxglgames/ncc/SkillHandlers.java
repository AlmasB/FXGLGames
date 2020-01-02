package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class SkillHandlers {

    private SkillHandlers() { }

    private static final Map<String, SkillHandler> handlers = new HashMap<>();

    static {
        add("Lightning Bolt", (user, skill, targets) -> {
            var dmg = skill.getAttr1();
            targets.get(0).getComponent(CardComponent.class).getHp().damage(dmg);
        });

        add("Courage", (user, skill, targets) -> {
            var heal = skill.getAttr1();
            targets.forEach(e -> e.getComponent(CardComponent.class).getHp().restore(heal));
        });
    }

    private static void add(String skillName, SkillHandler handler) {
        handlers.put(skillName, handler);
    }

    public static void handle(Entity user, Skill skill, List<Entity> targets) {
        handlers.getOrDefault(skill.getName(), (user1, skill1, targets1) -> {
            throw new IllegalArgumentException("Skill: " + skill.getName() + " does not have a handler!");
        }).onSkill(user, skill, targets);
    }
}
