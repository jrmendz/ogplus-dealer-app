package com.og.ogplus.dealerapp.controller;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.message.*;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.component.VideoReceiver;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.exception.AuthenticateException;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.game.model.GameResultModel;
import com.og.ogplus.dealerapp.service.*;
import com.og.ogplus.dealerapp.view.DealerAppView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

@Slf4j
@Lazy
public abstract class AbstractGameController
		implements AbstractGame.GameListener, MessageListener, GameServerClientService.Listener {
	private static final String ACTION_OK = "ok";
	private static final String ACTION_AUTO_DEAL = "auto deal";
	private static final String ACTION_CHANGE_DEALER = "change dealer";
	private static final String ACTION_PLAY_VIDEO = "play video";
	private static final String ACTION_MENU = "menu";
	private GameServerClientService gameServerClientService;
	private CameraService cameraService;
	protected Stage currentStage;
	protected Table table;
	private AppProperty appProperty;
	@Getter(AccessLevel.PROTECTED)
	private ThreadPoolTaskScheduler taskScheduler;
	@Getter(AccessLevel.PROTECTED)
	private Game game;
	@Getter(AccessLevel.PROTECTED)
	private ClientInteractService clientInteractService;
	@Getter(AccessLevel.PROTECTED)
	private SecurityService securityService;
	private SendCountDownTask sendCountDownTask;
	@Getter(AccessLevel.PROTECTED)
	private GameResultModel gameResultModel;
	private AtomicBoolean gameMessageSendOutFlag = new AtomicBoolean(false);
	private VideoReceiver videoReceiver;
	@Autowired
	private DealerAppView dealerAppView;

	public void init() {
	    if (checkGui()) {
		getLayout().addMenu("Turn On/Off Auto Deal Mode", (e) -> game.setAutoDeal(!game.isAutoDeal()));
		InputMap inputMap = getLayout().getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_OK);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), ACTION_CHANGE_DEALER);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), ACTION_AUTO_DEAL);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), ACTION_PLAY_VIDEO);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0), ACTION_MENU);

		ActionMap actionMap = getLayout().getRootPane().getActionMap();
		actionMap.put(ACTION_OK, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Received action command: Enter");
				game.resume();
			}
		});

		actionMap.put(ACTION_CHANGE_DEALER, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Received action command: {}", e.getActionCommand());
				getLayout().getChangeDealerButton().doClick();
			}
		});

		actionMap.put(ACTION_AUTO_DEAL, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Received action command: {}", e.getActionCommand());
				game.setAutoDeal(!game.isAutoDeal());
			}
		});

		actionMap.put(ACTION_PLAY_VIDEO, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Received action command: {}", e.getActionCommand());
				if (videoReceiver != null) {
					videoReceiver.refresh();
				}
			}
		});

		actionMap.put(ACTION_MENU, new AbstractAction() {
			private long lastTriggerTimeMillis;
			private int triggerCount = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Received action command: {}", e.getActionCommand());
				long now = System.currentTimeMillis();
				if (now - lastTriggerTimeMillis < 200) {
					triggerCount++;
				} else {
					triggerCount = 1;
				}
				lastTriggerTimeMillis = now;

				if (triggerCount >= 7) {
					triggerCount = 0;
					getLayout().showMenu();
				}
			}
		});

		getLayout().getChangeDealerButton().addActionListener(e -> taskScheduler.execute(() -> {
			do {
				try {
					String dealerCode = clientInteractService.getDealerCode();
					Dealer dealer = securityService.authDealer(dealerCode);
					game.setDealer(dealer);
					return;
				} catch (ClientInteractInterruptedException ex) {
					log.warn("Client Interact Interrupted");
					return;
				} catch (AuthenticateException ex) {
					clientInteractService.showErrorMessage(ex.getMessage());
				}
			} while (true);

		}));

		getLayout().getDetailsPanel().getChangeStageButton().addActionListener(e -> taskScheduler.execute(() -> {
			try {
				Stage stage = clientInteractService.showStage(appProperty.getGameCategory());// 用户设置的stage值
				if (stage != null) {
					if (!game.setTempStage(stage)) {
						clientInteractService.showErrorMessage("Invalid stage.");
					}
				}
			} catch (ClientInteractInterruptedException ex) {
				log.warn("Client Interact Interrupted");
			}
		}));

		getLayout().getAutoStartButton().addItemListener(e -> {
			int state = e.getStateChange();
			if (state == ItemEvent.SELECTED) {
				game.setAutoStart(true);
				game.resume();
				getLayout().getAutoStartButton().setForeground(Color.RED);
				getLayout().getStartButton().setEnabled(false);
			} else {
				game.setAutoStart(false);
				getLayout().getAutoStartButton().setForeground(Color.BLACK);
				getLayout().getStartButton().setEnabled(true);
			}
		});

		getLayout().getStartButton().addActionListener(e -> {
			game.resume();
		});

	    }
	}

	public DealerAppView getLayout() {
		return this.dealerAppView;
	}

	@Override
	public void onGameInitialized(Table table) {
		init();
		this.table = table;
		getLayout().setVisible(true);
		getLayout().setTableName(table.getNumber());
		getLayout().setStage(game.getStage().getSimpleFormat());

		gameResultModel.iterator().forEachRemaining(gameResult -> getLayout().showResult(gameResult));
	}

	@Override
	public void onDealerChange(Dealer oldDealer, Dealer newDealer) {
		getLayout().setDealerName(newDealer.getNickName());

		sendGameMessageOut(new UpdateDealerMessage(newDealer));
	}

	@Override
	public void onRoundStart(Stage stage) {
		if (stage.getRound() == 1
				|| (currentStage != null && !Objects.equals(currentStage.getShoe(), stage.getShoe()))) {
			getLayout().clearRoadMap();
		}
		this.currentStage = stage;

		getLayout().reset();
		getLayout().setStage(stage.getSimpleFormat());

		// 下一局开始时还原ChangeStageButton文本值
		if( getLayout().getDetailsPanel()!=null) {
            getLayout().getDetailsPanel().getChangeStageButton().setText("Setting");
        }

		sendGameMessageOut(new UpdateStageMessage(stage.getSimpleFormat(), stage));
	}

	@Override
	public void onStartBetting(LocalDateTime bettingEndTime) {
		getLayout().setStatus("Wait Betting");
		getLayout().startCountDown(bettingEndTime);

		sendGameMessageOut(new UpdateStatusMessage("BETTING TIME"));

		if (game.isBiddingMode()) {
			taskScheduler.schedule(() -> sendGameMessageOut(new ExtendMessage(true)), ZonedDateTime
					.of(bettingEndTime.minus(appProperty.getBiddingTime()), ZoneId.systemDefault()).toInstant());
		}

		sendCountDownTask = new SendCountDownTask(bettingEndTime);
		taskScheduler.execute(sendCountDownTask);
	}

	@Override
	public void onGameResult(GameResult gameResult) {
		sendGameMessageOut(generateProcessMessage(gameResult));
		sendGameMessageOut(generateBroadcastMessage(gameResult));
		sendGameMessageOut(new PayoutMessage(gameResult.getStage()));
		if (checkGui()) {
		SwingUtilities.invokeLater(() -> getLayout().showResult(gameResult));
		}
	}

	protected abstract Message generateProcessMessage(GameResult gameResult);

	protected abstract Message generateBroadcastMessage(GameResult gameResult);

	@Override
	public void onEndBetting() {
		if (game.isBiddingMode()) {
			sendGameMessageOut(new ExtendMessage(false));
		}
		sendGameMessageOut(new UpdateStatusMessage("NO MORE BETS"));
	}

	@Override
	public void onExtendBettingTime(LocalDateTime bettingEndTime) {
		if (game.isBiddingMode()) {
			sendGameMessageOut(new ExtendMessage(false));
			getLayout().stopCountDown();
			getLayout().startCountDown(bettingEndTime);

			if (sendCountDownTask != null) {
				sendCountDownTask.start = false;
				sendCountDownTask = new SendCountDownTask(bettingEndTime);
				taskScheduler.execute(sendCountDownTask);
			}
		}
	}

	@Override
	public void onRoundEnd() {
	    if (checkGui()) {
		SwingUtilities.invokeLater(() -> {
			getLayout().hideAlert();
			getLayout().hideResult();
		});
	    }
		sendGameMessageOut(new UpdateStatusMessage("ROUND COMPLETE"));
	}

	@Override
	public void onGamePause(String pauseMessage) {
		getLayout().setStatus(pauseMessage);
	}

	@Override
	public void onGameCancel(Stage stage) {
	    if (checkGui()) {
            SwingUtilities.invokeLater(() -> {
                getLayout().reset();
                getLayout().hideAlert();
                getLayout().hideResult();
                getLayout().setStatus("Cancel");
            });
        }

		if (sendCountDownTask != null) {
			sendCountDownTask.start = false;
		}
		sendGameMessageOut(new CancelGameMessage(stage));
		handleMessage(WARM, "Current game was cancelled.");
	}

	@Override
	public void onAlert(String alertMessage) {
		StringBuilder sb = new StringBuilder("<html>");
		String[] strArr = alertMessage.split(" ");
		int length = 0;
		for (String s : strArr) {
			if (length + s.length() > 20) {
				length = 0;
				sb.append("<br>").append(s).append(" ");
			} else {
				sb.append(s).append(" ");
			}
			length += s.length();
		}
		sb.append("</html>");

		getLayout().showAlert(sb.toString(), 36, Color.RED, Color.BLACK, null);
	}

	@Override
	public void onSetTempStage(Stage stage) {
		// 设置ChangeStageButton文本值为当前设置的stage值
		if(getLayout().getDetailsPanel()!=null) {
		    getLayout().getDetailsPanel().getChangeStageButton().setText(
	                String.format("<html>Setting<font color='red'>&nbsp;(%s)</font></html>", stage.getSimpleFormat()));
		}
	}

	@Override
	public void onAutoDealChanged(boolean isEnabled) {
		if (isEnabled) {
		    if(getLayout().getStatusLabel()!=null) {
		        getLayout().getStatusLabel().setBackground(Color.MAGENTA);
		    }
			clientInteractService.showMessage("Auto Mode ON");
		} else {
		    if(getLayout().getStatusLabel()!=null) {
		        getLayout().getStatusLabel().setBackground(Color.BLACK);
		    }
			clientInteractService.showMessage("Auto Mode OFF");
		}
	}

	@Override
	public void handleMessage(int type, String message) {
		switch (type) {
		case WARM:
			clientInteractService.showWarningMessage(message);
			break;
		case ERROR:
			clientInteractService.showErrorMessage(message);
			break;
		case INFO:
		default:
			clientInteractService.showMessage(message);
			break;
		}
	}

	@Override
	public void onSendGameMessageSuccess() {
		if (gameMessageSendOutFlag.get()) {
			gameMessageSendOutFlag.set(false);
			getLayout().hideAlert();
		}
	}

	@Override
	public void onSendGameMessageFailed() {
		gameMessageSendOutFlag.set(true);
		onAlert("Disconnect from Game Server");
	}

	protected void sendGameMessageOut(Message message) {
		if (gameServerClientService != null) {
			gameServerClientService.send(message);
		}
	}

	protected void transitCamera(CameraService.Mode mode) {
		if (cameraService != null) {
			getTaskScheduler().execute(() -> cameraService.switchCamera(mode));
		}
	}

    public boolean checkGui() {
        String gui = "true";
        if (appProperty == null) {
            gui = System.getProperty("app.gui");
        } else {
            gui = appProperty.getGui();
        }
        return Boolean.parseBoolean(gui);
    }

	@Autowired
	public void setAppProperty(AppProperty appProperty) {
		this.appProperty = appProperty;
	}

	@Autowired
	public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	@Autowired
	public void setGame(Game game) {
		this.game = game;
	}

	@Autowired
	public void setClientInteractService(ClientInteractService clientInteractService) {
		this.clientInteractService = clientInteractService;
	}

	@Autowired(required = false)
	public void setGameServerClientService(GameServerClientService gameServerClientService) {
		this.gameServerClientService = gameServerClientService;
	}

	@Autowired(required = false)
	public void setCameraService(CameraService cameraService) {
		this.cameraService = cameraService;
	}

	@Autowired
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	@Autowired
	public void setGameResultModel(GameResultModel gameResultModel) {
		this.gameResultModel = gameResultModel;
	}

	@Autowired(required = false)
	public void setVideoReceiver(VideoReceiver videoReceiver) {
		this.videoReceiver = videoReceiver;
	}

	protected class SendCountDownTask implements Runnable {
		LocalDateTime endTime;

		boolean start;

		protected SendCountDownTask(LocalDateTime endTime) {
			this.endTime = endTime;
			this.start = true;
		}

		@Override
		public void run() {
			if (start && LocalDateTime.now().isBefore(endTime)) {
				sendGameMessageOut(new TimerMessage(getCountDownTime()));
				taskScheduler.schedule(this, Instant.now().plusSeconds(1));
			} else {
				sendGameMessageOut(new TimerMessage(0));
			}
		}

		private int getCountDownTime() {
			return (int) Math.round(ChronoUnit.MILLIS.between(LocalDateTime.now(), endTime) / 1000.0);
		}
	}
}
