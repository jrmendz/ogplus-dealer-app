package com.og.ogplus.dealerapp.game.model;

import com.og.ogplus.common.model.Stage;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class GameResultModel {
    private static final int MAX_RECORD_SIZE = 100;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private List<GameResult> gameResultList = new LinkedList<>();

    public void addGameResult(GameResult gameResult) {
        try {
            lock.writeLock().lock();

            if (gameResultList.size() > 0 && isDifferentShoe(gameResult.getStage())) {
                clear();
            } else if (gameResultList.size() >= MAX_RECORD_SIZE) {
                gameResultList.remove(0);
            }

            gameResultList.add(gameResult);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setGameResults(List<GameResult> gameResults) {
        try {
            lock.writeLock().lock();
            clear();

            if (gameResultList.size() > MAX_RECORD_SIZE) {
                gameResultList.addAll(gameResults.subList(gameResults.size() - MAX_RECORD_SIZE, gameResults.size() - 1));
            } else {
                gameResultList.addAll(gameResults);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Iterator<GameResult> iterator() {
        try {
            lock.readLock().lock();
            return gameResultList.iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        try {
            lock.writeLock().lock();
            gameResultList.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isDifferentShoe(Stage stage) {
        Stage latestResultStage = gameResultList.get(gameResultList.size() - 1).getStage();

        if (stage.getShoe() == null) {
            return !latestResultStage.getDate().equals(stage.getDate());
        }

        if (stage.getShoe().equals(latestResultStage.getShoe())) {
            return !latestResultStage.getDate().equals(stage.getDate());
        } else {
            return true;
        }
    }

}
