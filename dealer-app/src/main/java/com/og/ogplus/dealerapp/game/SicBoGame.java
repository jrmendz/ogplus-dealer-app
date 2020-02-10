package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "SIC_BO")
@Component
public class SicBoGame extends AbstractGame {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Listener> listeners;
    private SicBoPoint[] points = new SicBoPoint[3];

    public SicBoGame(Table table) {
        super(table);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void reset() {
        IntStream.range(0, points.length).forEach(index -> points[index] = null);
    }

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

    @Override
    protected void deal() throws InterruptedException {
        listeners.forEach(listener -> getScheduler().execute(listener::onDice));
        do {
            Thread.sleep(500);
            if (isAutoDeal()) {
                autoSetResult();
            }
        } while (!hasResult());
    }

    @Override
    protected GameResult calculateResult() {
        return new SicBoGameResult(getStage().clone(), points.clone());
    }

    @Override
    protected boolean isCheckResultEnabled() {
        return false;
    }

    @Override
    protected boolean validateStage(Stage stage) {
        if (stage == null || stage.getShoe() != null) {
            return false;
        }

        return stage.getRound() > getStage().getRound();
    }

    private boolean hasResult() {
        try {
            lock.readLock().lock();
            return points[0] != null && points[1] != null && points[2] != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setPoints(@NotNull SicBoPoint point1, @NotNull SicBoPoint point2, @NotNull SicBoPoint point3) {
        try {
            lock.writeLock().lock();
            this.points[0] = point1;
            this.points[1] = point2;
            this.points[2] = point3;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void autoSetResult() {
        SicBoPoint[] points = SicBoPoint.values();
        SicBoPoint[] results = IntStream.range(0, 3)
                .mapToObj(i -> points[RandomUtils.nextInt(0, points.length)])
                .sorted()
                .toArray(SicBoPoint[]::new);

        setPoints(results[0], results[1], results[2]);
    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }


    public interface Listener extends GameListener {
        default void onDice() {
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SicBoGameResult implements GameResult {
        private static final long serialVersionUID = -4053201133212734056L;

        private Stage stage;

        private SicBoPoint[] points;
    }

}
