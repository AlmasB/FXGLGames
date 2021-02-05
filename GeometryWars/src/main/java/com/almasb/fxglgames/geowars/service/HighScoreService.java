package com.almasb.fxglgames.geowars.service;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.serialization.Bundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class HighScoreService extends EngineService {

    private IntegerProperty score = new SimpleIntegerProperty();
    private int numScoresToKeep = 10;

    private ArrayList<HighScoreData> highScores = new ArrayList<>();

    public IntegerProperty scoreProperty() {
        return score;
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public void incrementScore(int value) {
        setScore(getScore() + value);
    }

    /**
     * Remember current score with given tag and reset score to 0.
     */
    public void commit(String tag) {
        highScores.add(new HighScoreData(tag, getScore()));

        setScore(0);

        highScores = highScores.stream()
                .sorted(Comparator.comparingInt(HighScoreData::getScore).reversed())
                .limit(numScoresToKeep)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return list of high scores sorted in descending order
     */
    public List<HighScoreData> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public int getNumScoresToKeep() {
        return numScoresToKeep;
    }

    public void setNumScoresToKeep(int numScoresToKeep) {
        this.numScoresToKeep = numScoresToKeep;
    }

    @Override
    public void write(Bundle bundle) {
        bundle.put("highScores", highScores);
    }

    @Override
    public void read(Bundle bundle) {
        highScores = bundle.get("highScores");
    }

    public static class HighScoreData implements Serializable {
        private static final long serialVersionUID = 1;

        private final String tag;
        private final int score;

        private HighScoreData(String tag, int score) {
            this.tag = tag;
            this.score = score;
        }

        public String getTag() {
            return tag;
        }

        public int getScore() {
            return score;
        }
    }
}
