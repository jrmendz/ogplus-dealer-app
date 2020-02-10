package com.og.ogplus.dealerapp.view;

import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class CountDownPanel extends JPanel {
    private static Font font;

    static {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int fontSize = screenWidth >= 1920 ? 240 : screenWidth >= 1600 ? 220 : 180;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(new ClassPathResource("static/fonts/Courier Prime Sans Bold.ttf").getInputStream()));
            font = font.deriveFont(Font.BOLD, fontSize);
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("", Font.BOLD, fontSize);
        }
    }

    private JLabel countDownLabel;
    private Timer timer;
    private CountDownActionListener countDownActionListener;

    public CountDownPanel() {
        super(new GridBagLayout());

        countDownLabel = new JLabel("-", JLabel.CENTER);
        countDownLabel.setFont(font);
        countDownLabel.setPreferredSize(new Dimension(300, 200));
        countDownLabel.setMinimumSize(new Dimension(300, 150));
        countDownLabel.setForeground(Color.ORANGE);
        add(countDownLabel);

        countDownActionListener = new CountDownActionListener();
        timer = new Timer(1000, countDownActionListener);
        timer.setRepeats(true);

        setOpaque(false);
    }


    public void startCountDownTimer(final LocalDateTime endTime) {
        countDownActionListener.endTime = endTime;
        timer.start();
        timer.setInitialDelay(0);
    }

    public void stopCountDownTimer() {
        timer.stop();
        countDownLabel.setText("-");
    }

    private class CountDownActionListener implements ActionListener {
        private LocalDateTime endTime;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (LocalDateTime.now().isBefore(endTime)) {
                countDownLabel.setText(String.valueOf(getCountDownTime()));
            } else {
                countDownLabel.setText("0");
                stopCountDownTimer();
            }
        }

        private int getCountDownTime() {
            return (int) Math.round(ChronoUnit.MILLIS.between(LocalDateTime.now(), endTime) / 1000.0);
        }
    }

}
