package com.og.ogplus.dealerapp.view.baccarat;

import com.og.ogplus.dealerapp.view.CardView;
import com.og.ogplus.dealerapp.view.GameImages;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class CardsPanel extends JPanel {

    private final int cardWidth;
    private final int cardHeight;
    private CardView playerCard1;
    private CardView playerCard2;
    private CardView playerCard3;
    private CardView bankerCard1;
    private CardView bankerCard2;
    private CardView bankerCard3;
    @Getter
    private JLabel playerTotal;
    @Getter
    private JLabel bankerTotal;

    public CardsPanel() {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        float scale = screenWidth >= 1920 ? 1 : screenWidth >= 1600 ? 4 / 5f : screenWidth >= 1360 ? 3 / 5f :2 / 3f;
        cardWidth = (int) (CardView.DEFAULT_WIDTH * scale);
        cardHeight = (int) (CardView.DEFAULT_HEIGHT * scale);

        int cardMargin = (int) (5 * scale);

        setLayout(new GridBagLayout());
        add(new JLabel(), new GridBagConstraints());
        add(new JLabel(), new GridBagConstraints());
        add(new JLabel(), new GridBagConstraints());
        add(new JLabel(), new GridBagConstraints());
        add(new JLabel(), new GridBagConstraints());
        add(new JLabel(), new GridBagConstraints());

        JPanel bankerPanel = new JPanel(new GridBagLayout());
        bankerPanel.setOpaque(false);
        bankerPanel.add(new JLabel(), new GridBagConstraints());
        bankerPanel.add(new JLabel(), new GridBagConstraints());
        bankerPanel.add(new JLabel(), new GridBagConstraints());

        bankerTotal = new JLabel("", JLabel.CENTER);
        bankerTotal.setPreferredSize(new Dimension(100, 100));
        bankerTotal.setFont(new Font("Serif", Font.BOLD, 72));
        bankerTotal.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
        bankerTotal.setForeground(Color.LIGHT_GRAY);
        bankerPanel.add(bankerTotal,
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JLabel bankerLabel = new JLabel("Banker", JLabel.CENTER);
        bankerLabel.setForeground(Color.RED);
        bankerLabel.setPreferredSize(new Dimension(400, 100));
        bankerLabel.setFont(new Font("Serif", Font.BOLD, 96));
        bankerPanel.add(bankerLabel,
                new GridBagConstraints(1, 1, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        bankerCard3 = new CardView(cardHeight, cardHeight) {
            @Override
            public void paintComponent(Graphics g) {
                ((Graphics2D) g).rotate(-Math.PI / 2);
                g.translate(-getHeight(), -0);
                super.paintComponent(g);
            }

            @Override
            protected int getImageScaledHeight() {
                return cardHeight;
            }

            @Override
            protected int getImageScaledWidth() {
                return cardWidth;
            }
        };
        bankerPanel.add(bankerCard3,
                new GridBagConstraints(0, 2, 1, 1, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin * 10), 0, 0));

        bankerCard2 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard2,
                new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        bankerCard1 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard1,
                new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));


        JPanel playerPanel = new JPanel(new GridBagLayout());
        playerPanel.setOpaque(false);
        playerPanel.add(new JLabel(), new GridBagConstraints());
        playerPanel.add(new JLabel(), new GridBagConstraints());
        playerPanel.add(new JLabel(), new GridBagConstraints());

        JLabel playerLabel = new JLabel("Player", JLabel.CENTER);
        playerLabel.setForeground(Color.decode("#1195ea"));
        playerLabel.setPreferredSize(new Dimension(400, 100));
        playerLabel.setFont(new Font("Serif", Font.BOLD, 96));
        playerPanel.add(playerLabel,
                new GridBagConstraints(3, 1, 2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        playerTotal = new JLabel("", JLabel.CENTER);
        playerTotal.setPreferredSize(new Dimension(100, 100));
        playerTotal.setFont(new Font("Serif", Font.BOLD, 72));
        playerTotal.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
        playerTotal.setForeground(Color.LIGHT_GRAY);
        playerPanel.add(playerTotal,
                new GridBagConstraints(5, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        playerCard1 = new CardView(cardWidth, cardHeight);
        playerPanel.add(playerCard1,
                new GridBagConstraints(3, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        playerCard2 = new CardView(cardWidth, cardHeight);
        playerPanel.add(playerCard2,
                new GridBagConstraints(4, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        playerCard3 = new CardView(cardHeight, cardHeight) {
            @Override
            public void paintComponent(Graphics g) {
                ((Graphics2D) g).rotate(-Math.PI / 2);
                g.translate(-getHeight(), -0);
                super.paintComponent(g);
            }

            @Override
            protected int getImageScaledHeight() {
                return cardHeight;
            }

            @Override
            protected int getImageScaledWidth() {
                return cardWidth;
            }
        };
        playerPanel.add(playerCard3,
                new GridBagConstraints(5, 2, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin * 10, 0, cardMargin), 0, 0));

        add(bankerPanel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(playerPanel,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        setOpaque(false);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CardsPanel cardsPanel = new CardsPanel();
        frame.add(cardsPanel);
//        frame.setSize(1920, 540);
        frame.setSize(1360, 768 / 2);
        frame.setVisible(true);
        cardsPanel.getBankerCard3().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getBankerCard2().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getBankerCard1().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getPlayerCard1().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getPlayerCard2().setCardImage(GameImages.GAME_CARDS.get("AH"));
        cardsPanel.getPlayerCard3().setCardImage(GameImages.GAME_CARDS.get("AH"));

    }
}
