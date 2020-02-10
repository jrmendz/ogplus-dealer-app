package com.og.ogplus.dealerapp.view;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.og.ogplus.dealerapp.game.model.GameResult;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@ConditionalOnExpression("${app.gui:true}")
@Component
public abstract class DealerAppWindowsLayout extends JFrame implements DealerAppView {
    @Getter
    private DetailsPanel detailsPanel;

    private CountDownPanel countDownPanel;

    @Getter
    private JLabel statusLabel;

    private JLabel videoLoadingLabel;

    private VideoPanel videoDisplayPanel;

    private RoadMapPanel roadMapPanel;

    @Getter
    private JButton startButton;

    @Getter
    private JToggleButton autoStartButton;

    @Getter
    private JButton changeDealerButton;

    private JLabel alertLabel;

    private Timer alertLabelTimer;

    private ResultDialog resultDialog;

    private JPopupMenu popupMenu;

    public DealerAppWindowsLayout() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(GameImages.LOGO);
        setAlwaysOnTop(true);
        this.getRootPane();
    }

    @PostConstruct
    private void addComponent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.decode("#3c3f41"));

        mainPanel.add(new DateTimePanel(),
                new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        detailsPanel = new DetailsPanel();
        mainPanel.add(detailsPanel,
                new GridBagConstraints(0, 1, 1, 1, 1, 2, NORTH, BOTH, new Insets(0, 20, 0, 0), 0, 0));

        countDownPanel = new CountDownPanel();
        mainPanel.add(countDownPanel,
                new GridBagConstraints(0, 2, 1, 1, 1, 2, CENTER, HORIZONTAL, new Insets(0, 0, -20, 0), 0, 0));

        try {
            videoLoadingLabel = new JLabel(new ImageIcon(new ClassPathResource("static/images/loading2.gif").getURL()), JLabel.CENTER);
            mainPanel.add(videoLoadingLabel,
                    new GridBagConstraints(1, 0, 1, 3, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        JPanel videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBackground(Color.BLACK);
        videoPanel.setPreferredSize(new Dimension(600, 450));
        videoPanel.setMinimumSize(new Dimension(400, 300));
        videoDisplayPanel = new VideoPanel();
        videoDisplayPanel.setBackground(new Color(0, 0, 0, 0));
        videoPanel.add(videoDisplayPanel, BorderLayout.CENTER);
        mainPanel.add(videoPanel,
                new GridBagConstraints(1, 0, 1, 3, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        panel.add(new ControlPanel(),
                new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        alertLabel = new JLabel();
        alertLabel.setVisible(false);
        alertLabel.setHorizontalAlignment(JLabel.CENTER);
        alertLabel.setOpaque(true);
        panel.add(alertLabel,
                new GridBagConstraints(0, 1, 1, 1, 1, 3, CENTER, BOTH, new Insets(20, 20, 20, 20), 0, 0));

        roadMapPanel = generateRoadMapPanel();
        panel.add(roadMapPanel,
                new GridBagConstraints(0, 1, 1, 1, 1, 3, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        mainPanel.add(panel,
                new GridBagConstraints(2, 0, 1, 3, 1, 1, CENTER, BOTH, new Insets(10, 0, 10, 0), 0, 0));

        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 72));
        statusLabel.setBackground(Color.BLACK);
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel,
                new GridBagConstraints(0, 3, 3, 1, 1, 1.5, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel p = new JPanel(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        p.setPreferredSize(new Dimension(screenSize.width, screenSize.height / 11 * 5));
        p.setMinimumSize(new Dimension(screenSize.width, screenSize.height / 11 * 5));
        p.add(getGameComponent(), BorderLayout.CENTER);
        p.setOpaque(false);
        mainPanel.add(p, new GridBagConstraints(0, 4, 3, 1, 1, 5, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));

        add(mainPanel);

        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));
        addMenu("Close Dealer App", e -> System.exit(0));
    }

    public void showMenu() {
        popupMenu.show(this, 0, 0);
    }

    public void addMenu(String text, ActionListener actionListener) {
        JMenuItem item = new JMenuItem("- " + text);
        item.setFont(new Font("", Font.PLAIN, 24));
        item.addActionListener(actionListener);
        popupMenu.add(item);
        popupMenu.pack();
    }

    public void addShuffleButtonAction(ActionListener actionListener) {
        if (actionListener != null) {
            detailsPanel.getShuffleButton().setVisible(true);
            detailsPanel.getShuffleButton().addActionListener(actionListener);
        } else {
            detailsPanel.getShuffleButton().setVisible(false);
        }
        revalidate();
        repaint();
    }

    protected RoadMapPanel generateRoadMapPanel() {
        return new RoadMapPanel();
    }

    public void reset() {
        stopCountDown();
        hideAlert();
        hideResult();
    }

    public void clearRoadMap() {
        roadMapPanel.reset();
    }

    protected abstract JComponent getGameComponent();

    public void setStatus(String status) {
        statusLabel.setText(status);
        revalidate();
        repaint();
    }

    public void startCountDown(LocalDateTime endTime) {
        countDownPanel.startCountDownTimer(endTime);
    }

    public void stopCountDown() {
        countDownPanel.stopCountDownTimer();
    }

    public void setTableName(String tableName) {
        detailsPanel.getTableNumber().setText(tableName);
    }

    public void setDealerName(String name) {
        if (name.length() >= 15) {
            int i = name.lastIndexOf(" ", 12);
            if (i != -1) {
                detailsPanel.getDealerName()
                        .setText(String.format("<html>%s<br>%s<html>", name.substring(0, i), limitLength(name.substring(i), 15)));
            } else {
                detailsPanel.getDealerName().setText(limitLength(name, 12));
            }
        } else {
            detailsPanel.getDealerName().setText(name);
        }

    }

    private String limitLength(String s, int maxLength) {
        if (s.length() > maxLength) {
            return s.substring(0, 12) + "...";
        } else {
            return s;
        }
    }

    public void setStage(String stage) {
        detailsPanel.getTableStage().setText(stage);
    }

    public String getStage() {
        return detailsPanel.getTableStage().getText();
    }

    public void showAlert(String msg, Color bgColor, Color fgColor) {
        showAlert(msg, 36, bgColor, fgColor, Duration.ofSeconds(5));
    }

    public void showAlert(String msg, int size, Color bgColor, Color fgColor, Duration showUp) {
        showAlert(msg, new Font("", Font.BOLD, size), bgColor, fgColor, showUp);
    }

    public void showAlert(String msg, Font font, Color bgColor, Color fgColor, Duration showUp) {
        alertLabel.setText(msg);
        alertLabel.setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 220));
        alertLabel.setForeground(fgColor);
        alertLabel.setFont(font);
        alertLabel.setVisible(true);
        revalidate();
        repaint();

        if (showUp != null) {
            alertLabelTimer = new Timer((int) showUp.getSeconds() * 1000, e -> alertLabel.setVisible(false));
            alertLabelTimer.setRepeats(false);
            alertLabelTimer.start();
        }
    }

    public void hideAlert() {
        alertLabel.setVisible(false);
        if (alertLabelTimer != null) {
            alertLabelTimer.stop();
        }
        revalidate();
        repaint();
    }

    public void showResult(String msg, Color bgColor, Color fgColor) {
        showResult(msg, 36, bgColor, fgColor);
    }

    public void showResult(String msg, int size, Color bgColor, Color fgColor) {
        showResult(msg, new Font("", Font.BOLD, size), bgColor, fgColor);
    }

    public void showResult(String msg, Font font, Color bgColor, Color fgColor) {
        resultDialog.showResult(msg, font, bgColor, fgColor);
        revalidate();
        repaint();
    }

    public void hideResult() {
        resultDialog.hideResult();
        revalidate();
        repaint();
    }

    public void showResult(GameResult gameResult) {
        setStatus(getResultText(gameResult));
        addRoadMap(parseRoadMapImage(gameResult));
        revalidate();
        repaint();
    }

    protected abstract String getResultText(GameResult gameResult);

    protected abstract Image parseRoadMapImage(GameResult gameResult);

    protected void addRoadMap(Image... images) {
        for (Image image : images) {
            if (image != null) {
                roadMapPanel.addRoadMap(image);
            }
        }
    }

    @Lazy
    @Autowired
    public void setResultDialog(ResultDialog resultDialog) {
        this.resultDialog = resultDialog;
    }

    public void showVideoImage(BufferedImage image) {
        if (videoDisplayPanel.isVisible()) {
            if (image == null) {
                videoDisplayPanel.display(null);
            } else {
                if (videoLoadingLabel.isVisible()) {
                    setVideoLoadingVisible(false);
                }
                if (EventQueue.isDispatchThread()) {
                    videoDisplayPanel.display(image);
                } else {
                    EventQueue.invokeLater(() -> videoDisplayPanel.display(image));
                }
            }
        }
    }

    public void setVideoLoadingVisible(boolean isVisible) {
        if (EventQueue.isDispatchThread()) {
            videoLoadingLabel.setVisible(isVisible);
        } else {
            EventQueue.invokeLater(() -> videoLoadingLabel.setVisible(isVisible));
        }
    }

    private class ControlPanel extends JPanel {
        private final Dimension BUTTON_PREFER_SIZE = new Dimension(150, 100);
        private final Dimension BUTTON_MINIMUM_SIZE = new Dimension(150, 70);

        private final Font FONT = new Font("", Font.BOLD, 20);

        private ControlPanel() {
            setLayout(new GridBagLayout());
            setOpaque(false);

            changeDealerButton = new JButton("<html>Change<br>&nbsp;Dealer</html>");
            changeDealerButton.setFont(FONT);
            changeDealerButton.setPreferredSize(BUTTON_PREFER_SIZE);
            changeDealerButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
            changeDealerButton.setFocusable(false);
            add(changeDealerButton,
                    new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

            autoStartButton = new JToggleButton("Auto Start");
            autoStartButton.setFont(FONT);
            autoStartButton.setPreferredSize(BUTTON_PREFER_SIZE);
            autoStartButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
            autoStartButton.setFocusable(false);
            add(autoStartButton,
                    new GridBagConstraints(1, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

            startButton = new JButton("Start");
            startButton.setFont(FONT);
            startButton.setPreferredSize(BUTTON_PREFER_SIZE);
            startButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
            startButton.setFocusable(false);
            add(startButton,
                    new GridBagConstraints(2, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

            setMinimumSize(new Dimension(480, 70));
            setPreferredSize(new Dimension(600, 100));

            pack();
        }
    }



}
