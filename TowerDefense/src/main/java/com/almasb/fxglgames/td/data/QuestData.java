package com.almasb.fxglgames.td.data;

/**
 * @param desc how to complete the quest
 * @param varName variable name
 * @param varValue variable value to complete the quest
 * @param reward how much money is given after this quest is finished
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public record QuestData(
        String desc,
        String varName,
        Object varValue,
        int reward
) { }
