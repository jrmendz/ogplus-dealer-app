package com.og.ogplus.dealerapp.view.dt;

import com.og.ogplus.dealerapp.view.CardView;
import com.og.ogplus.dealerapp.view.GameImages;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

@Getter
public class CardsPanel extends JPanel {
    private final int cardWidth;
    private final int cardHeight;

    private CardView dragonCardView;
    private CardView tigerCardView;

    public CardsPanel() {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        float scale = screenWidth >= 1920 ? 1 : screenWidth >= 1600 ? 4 / 5f : screenWidth >= 1360 ? 3 / 5f :2 / 3f;

        cardWidth = (int) (CardView.DEFAULT_WIDTH * scale);
        cardHeight = (int) (CardView.DEFAULT_HEIGHT * scale);

        int cardMargin = (int) (5 * scale);

        dragonCardView = new CardView(cardWidth, cardHeight);
        tigerCardView = new CardView(cardWidth, cardHeight);

        setLayout(new GridBagLayout());

        JLabel tigerLabel = new JLabel("Tiger", JLabel.CENTER);
        tigerLabel.setForeground(Color.RED);
        tigerLabel.setFont(new Font("Serif", Font.BOLD, 96));
        tigerLabel.setPreferredSize(new Dimension(400, 100));
        add(tigerLabel,
                new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 150, 0));

        JLabel dragonLabel = new JLabel("Dragon", JLabel.CENTER);
        dragonLabel.setForeground(Color.decode("#1195ea"));
        dragonLabel.setPreferredSize(new Dimension(400, 100));
        dragonLabel.setFont(new Font("Serif", Font.BOLD, 96));
        add(dragonLabel,
                new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 150, 0));

        add(tigerCardView,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, NONE, new Insets(cardMargin * 10, 0, 0, 0), 0, 0));

        add(dragonCardView,
                new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, NONE, new Insets(cardMargin * 10, 0, 0, 0), 0, 0));

        setOpaque(false);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CardsPanel cardsPanel = new CardsPanel();
        frame.add(cardsPanel);
//        frame.setSize(1920, 540);
        frame.setSize(1360, 768 / 2);
        frame.setVisible(true);
        cardsPanel.getDragonCardView().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getTigerCardView().setCardImage(GameImages.GAME_CARDS.get("AH"));
    }
}
