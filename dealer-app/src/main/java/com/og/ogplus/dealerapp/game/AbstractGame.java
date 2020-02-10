package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.exception.GameCancelledException;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.MessageListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class AbstractGame implements Game {
    static final int DATA_PROCESS_PROVE_DELAY = 100;    //Cause pub/sub pattern is not sequential, add little delay to achieve it
    private static final Duration DEFAULT_ROUND_DELAY = Duration.of(6, ChronoUnit.SECONDS);
    private final AtomicBoolean stopFlag = new AtomicBoolean(true);
    private final AtomicBoolean pause = new AtomicBoolean(false);
    private final AtomicBoolean cancel = new AtomicBoolean(false);
    private final AtomicBoolean allowCancel = new AtomicBoolean(false);
    private final AtomicBoolean autoStart = new AtomicBoolean(false);
    private final AtomicBoolean autoDeal = new AtomicBoolean(false);
    private final AtomicBoolean autoDealChangeable = new AtomicBoolean(true);
    private final AtomicInteger extendBettingTime = new AtomicInteger(0);  // The extended betting time in current round
    @Getter(AccessLevel.PROTECTED)
    private AppProperty appProperty;
    @Getter(AccessLevel.PROTECTED)
    private ThreadPoolTaskScheduler scheduler;
    private boolean hasInitialized = false;
    private GameIdentity gameIdentity;
    @Getter
    private Table table;
    @Getter
    private Dealer dealer;
    private Stage stage;
    @Getter
    private Stage tempStage;
    private List<GameListener> gameListeners;

    @Getter(AccessLevel.PROTECTED)
    private boolean allowChangeGameResult = false;

    @Getter(AccessLevel.PROTECTED)
    private Lock changeLock = new ReentrantLock();
    private LocalDateTime bettingEndTime;
    private List<MessageListener> messageListeners;

    public AbstractGame(Table table) {
        this.table = table;
        this.gameIdentity = new GameIdentity(table.getGameCode().getCode(), table.getNumber());
    }


    @Override
    public final void initialize() {
        if (this.stage == null) {
            this.stage = new Stage(LocalDate.now(), isShoeNeeded() ? 1 : null, 0);
        }
        init();
        if(!checkeGui()) {
            setAutoDeal(true);
            setAutoDealChangeable(false);
        }
        hasInitialized = true;
        gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onGameInitialized(table)));
    }

    /**
     * If the game need shoe in stage
     *
     * @return true: stage will include shoe number, false: shoe will be null
     * @see Stage
     */
    protected boolean isShoeNeeded() {
        return true;
    }

    @Override
    public synchronized void start() {
        if (!hasInitialized) {
            throw new RuntimeException("Should invoke initialize() before start the game.");
        }

        stopFlag.set(false);
        try {
            do {
                checkPause("PAUSE", Duration.of(500, ChronoUnit.MILLIS));
                checkAutoStart();
                doBeforeRoundStart();
                Thread.sleep(250);
                gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onRoundStart(stage.clone())));
                Thread.sleep(500);
                doAfterRoundStart();
                Thread.sleep(250);

                try {
                    waitBettingAndDeal();
                    Thread.sleep(250);
                    gameListeners.forEach(gameListener -> scheduler.execute(gameListener::onRoundEnd));
                    Thread.sleep(500);
                } catch (GameCancelledException e) {
                    doCancel();
                }
                doAfterRoundEnd();
            } while (!stopFlag.get());
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    protected void doCancel() {
        gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onGameCancel(getStage().clone())));
    }

    protected void waitBettingAndDeal() throws GameCancelledException, InterruptedException {
        Future gameFlowFuture = scheduler.submit(() -> {
            try {
                waitBetting();
                Thread.sleep(2000);
                deal();
                Thread.sleep(100);
                doBeforeRoundEnd();
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });

        Future acceptCancelFuture = scheduler.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (cancel.get()) {
                    return null;
                }
                Thread.sleep(100);
            }
            throw new InterruptedException();
        });

        try {
            do {
                if (gameFlowFuture.isDone()) {
                    return;
                } else if (acceptCancelFuture.isDone()) {
                    throw new GameCancelledException();
                } else {
                    Thread.sleep(100);
                }
            } while (true);
        } finally {
            acceptCancelFuture.cancel(true);
            gameFlowFuture.cancel(true);
        }
    }

    protected void toNextRound() {
        if (tempStage == null) {// 没有设置stage值
            stage.setRound(stage.getRound() + 1);
        } else {
            stage.setShoe(tempStage.getShoe());
            stage.setRound(tempStage.getRound());
            tempStage = null;// 重置stage值
        }
    }

    @Override
    public void pause() {
        this.pause.set(true);
    }

    @Override
    public void resume() {
        this.pause.set(false);
    }

    @Override
    public void setDealer(Dealer dealer) {
        if (this.dealer == null || !this.dealer.equals(dealer)) {
            Dealer old = this.dealer;
            this.dealer = dealer;
            gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onDealerChange(old, dealer)));
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public boolean setTempStage(Stage stage) {
        if (validateStage(stage)) {
            this.tempStage = stage;
            gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onSetTempStage(stage)));
            return true;
        }
        return false;
    }

    /**
     * 抽象验证stage方法, 各自游戏单独重写
     *
     * @param stage
     * @return true, false
     */
    protected abstract boolean validateStage(Stage stage);

    public boolean isPause() {
        return pause.get();
    }

    @Override
    public void stop() {
        stopFlag.set(true);
    }

    @Override
    public boolean cancel() {
        if (allowCancel.get()) {
            cancel.set(true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setAutoStart(boolean isEnabled) {
        this.autoStart.set(isEnabled);
    }

    @Override
    public boolean isAutoDeal() {
        return this.autoDeal.get();
    }

    @Override
    public void setAutoDeal(boolean isEnabled) {
        if (isAutoDealChangeable()) {
            this.autoDeal.set(isEnabled);
            gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onAutoDealChanged(isEnabled)));

        }
    }

    @Override
    public boolean isBiddingMode() {
        return appProperty.isBiddingMode();
    }

    //In bidding mode, player can ask more time to bet
    @Override
    public void extendBettingTime() {
        synchronized (extendBettingTime) {
            if (isBiddingMode() && extendBettingTime.get() == 0) {
                extendBettingTime.set((int) appProperty.getBiddingExtendTime().getSeconds());
                gameListeners.forEach(gameListener -> scheduler
                        .execute(() -> gameListener.onExtendBettingTime(bettingEndTime.plusSeconds(extendBettingTime.get()))));
            }
        }
    }

    @PreDestroy
    @Override
    public void destroy() {

    }

    @Override
    public GameIdentity getGameIdentity() {
        return this.gameIdentity;
    }

    public Stage getStage() {
        return stage;
    }

    protected abstract void init();

    protected Duration getRoundDelay() {
        Integer nextRoundDelayTime = table.getMeta().getNextRoundDelay();
        return nextRoundDelayTime != null ? Duration.of(nextRoundDelayTime, ChronoUnit.MILLIS) : DEFAULT_ROUND_DELAY;
    }

    protected Duration getBettingTime() {
        return Duration.of(table.getMaxTime(), ChronoUnit.SECONDS);
    }

    protected void pause(String pauseMessage) throws InterruptedException {
        pause(pauseMessage, Duration.of(500, ChronoUnit.MILLIS));
    }

    protected void pause(String pauseMessage, Duration checkDuration) throws InterruptedException {
        pause();
        checkPause(pauseMessage, checkDuration);
    }

    protected void doBeforeRoundStart() throws InterruptedException {
        LocalDate today = LocalDate.now();
        if (!today.isEqual(stage.getDate())) {
            stage.setRound(0);
            stage.setDate(today);
        }
        reset();
        toNextRound();
    }

    protected void doAfterRoundStart() {
        changeLock.lock();
        allowChangeGameResult = true;
        changeLock.unlock();
        allowCancel.set(true);
    }

    protected void doBeforeRoundEnd() throws InterruptedException {
        if (isCheckResultEnabled()) {
            pause("Press ENTER To Confirm Game Result");
        }
        changeLock.lock();
        allowChangeGameResult = false;
        changeLock.unlock();
        allowCancel.set(false);

        gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onGameResult(calculateResult())));
    }

    protected abstract GameResult calculateResult();

    protected void doAfterRoundEnd() throws InterruptedException {
        Thread.sleep(getRoundDelay().toMillis());
    }

    protected boolean isCheckResultEnabled() {
        return true;
    }

    protected void waitBetting() throws InterruptedException {
        extendBettingTime.set(0);
        bettingEndTime = LocalDateTime.now().plus(getBettingTime());
        gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onStartBetting(bettingEndTime)));

        do {
            Thread.sleep(500);
        } while (isWaitBetting());

        Thread.sleep(100);  //delay for wait count down time send out
        gameListeners.forEach(gameListener -> scheduler.execute(gameListener::onEndBetting));
    }
    
    protected boolean isWaitBetting() {
    	return LocalDateTime.now().isBefore(bettingEndTime.plusSeconds(extendBettingTime.get()));
    }

    protected void updateGameInfo() {
        gameListeners.forEach(gameListener -> scheduler.execute(gameListener::onUpdateGameInfo));
    }

    protected void showMessage(String message) {
        messageListeners.forEach(messageListener -> messageListener.handleMessage(MessageListener.INFO, message));
    }

    protected void showWarmMessage(String message) {
        messageListeners.forEach(messageListener -> messageListener.handleMessage(MessageListener.WARM, message));
    }

    protected void showErrorMessage(String message) {
        messageListeners.forEach(messageListener -> messageListener.handleMessage(MessageListener.ERROR, message));
    }

    protected void alert(String alertMessage) {
        if (StringUtils.isBlank(alertMessage)) {
            return;
        }
        gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onAlert(alertMessage)));
    }

    protected boolean isAutoDealChangeable() {
        return autoDealChangeable.get();
    }

    protected void setAutoDealChangeable(boolean isEnabled) {
        autoDealChangeable.set(isEnabled);
    }

    protected void reset() {
        cancel.set(false);
    }

    protected abstract void deal() throws InterruptedException;

    private void checkAutoStart() throws InterruptedException {
        if (!autoStart.get()) {
            pause("Press START To Begin Next Round");
        }
    }
    
    public boolean checkeGui() {
        String gui=System.getProperty("app.gui");
       return Boolean.parseBoolean(gui);
    }
    
    private void checkPause(String pauseMessage, Duration checkDuration) throws InterruptedException {
        if (pause.get()) {
            gameListeners.forEach(gameListener -> scheduler.execute(() -> gameListener.onGamePause(pauseMessage)));
            do {
                if (isAutoDeal()) {
                    resume();
                }
                try {
                    Thread.sleep(checkDuration.toMillis());
                } catch (InterruptedException e) {
                    resume();
                    throw e;
                }
            } while (pause.get());
            gameListeners.forEach(gameListener -> scheduler.execute(gameListener::onGameResume));
        }
    }

    @Lazy
    @Autowired
    public void setGameListeners(List<GameListener> gameListeners) {
        this.gameListeners = gameListeners;
    }

    @Autowired
    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    @Autowired
    public void setScheduler(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Lazy
    @Autowired
    public void setMessageListeners(List<MessageListener> messageListeners) {
        this.messageListeners = messageListeners;
    }

    public interface GameListener {

        default void onGameInitialized(Table table) {
        }

        default void onRoundStart(Stage stage) {
        }

        default void onStartBetting(LocalDateTime bettingEndTime) {
        }

        default void onEndBetting() {
        }

        default void onRoundEnd() {
        }

        default void onGameResult(GameResult gameResult) {
        }

        default void onDealerChange(Dealer oldDealer, Dealer newDealer) {
        }

        default void onGamePause(String pauseMessage) {
        }

        default void onGameResume() {
        }

        default void onGameCancel(Stage stage) {
        }

        default void onUpdateGameInfo() {
        }

        default void onExtendBettingTime(LocalDateTime bettingEndTime) {
        }

        default void onAutoDealChanged(boolean isEnabled) {
        }

        default void onAlert(String alertMessage) {
        }

        default void onSetTempStage(Stage stage) {
        }

    }
}
