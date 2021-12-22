package com.almasb.fxglgames.td;

public record TowerData(
        String name,
        String imageName,
        String projectileImageName,
        int attack,
        double attackRate,
        int cost,
        boolean isSplashDamage
) { }
