package com.og.ogplus.dealerapp.view;

import javax.swing.*;
import java.awt.*;

public class CardView extends JPanel {

    public static final int DEFAULT_HEIGHT = 315;
    public static final int DEFAULT_WIDTH = 250;

    private int width;
    private int height;

    private JLabel cardLabel;
    private JLabel borderLabel;

    private Timer timer;

    public CardView() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public CardView(int width, int height) {
        this.width = width;
        this.height = height;

        setLayout(new GridBagLayout());
        borderLabel = new JLabel();
        borderLabel.setPreferredSize(new Dimension(width, height));
        borderLabel.setOpaque(false);
        borderLabel.setVisible(false);
        borderLabel.setIcon(new ImageIcon(GameImages.CARD_BORDER.getScaledInstance(getImageScaledWidth(), getImageScaledHeight(), Image.SCALE_SMOOTH)));
        add(borderLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        cardLabel = new JLabel();
        cardLabel.setPreferredSize(new Dimension(width, height));
        cardLabel.setOpaque(false);
        add(cardLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        setCardImage(GameImages.CARD_SLOT);
//        setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

        timer = new Timer(300, e -> {
            borderLabel.setVisible(!borderLabel.isVisible());
            repaint();
            revalidate();
        });
        timer.setRepeats(true);

        setOpaque(false);
    }

    public void setCardImage(Image image) {
        if (image == null) {
            cardLabel.setIcon(null);
        } else {
            cardLabel.setIcon(new ImageIcon(image.getScaledInstance(getImageScaledWidth(), getImageScaledHeight(), Image.SCALE_SMOOTH)));
        }

        revalidate();
        repaint();
    }

    public synchronized void blinkBorder() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public synchronized void unBlinkBorder() {
        if (timer.isRunning()) {
            timer.stop();
            borderLabel.setVisible(false);
        }
    }


    protected int getImageScaledWidth() {
        return this.width;
    }

    protected int getImageScaledHeight() {
        return this.height;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CardView cardView = new CardView();
        frame.add(cardView);
        frame.pack();
        frame.setVisible(true);
        cardView.setCardImage(GameImages.CARD_SLOT_ACTIVE);
        cardView.blinkBorder();
    }
}
