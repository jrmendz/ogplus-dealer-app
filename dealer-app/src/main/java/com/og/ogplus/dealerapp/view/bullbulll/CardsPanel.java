package com.og.ogplus.dealerapp.view.bullbulll;


import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.dealerapp.view.CardView;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class CardsPanel extends JPanel {
    private CardView bankerCard1;
    private CardView bankerCard2;
    private CardView bankerCard3;
    private CardView bankerCard4;
    private CardView bankerCard5;

    private CardView firstCard;

    private final int cardWidth;
    private final int cardHeight;

    private CardsPackets packets1, packets2, packets3;

    private JLabel bankerResult;

    private int fontSize = 36;

    /**
     * Create the panel.
     */
    public CardsPanel() {

        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        float scale = screenWidth >= 1920 ? 1 : screenWidth >= 1600 ? 4 / 5f : screenWidth >= 1360 ? 2 / 3f : 3 / 5f;
        cardWidth = (int) (160 * scale);
        cardHeight = (int) (150 * scale);

        setLayout(new GridBagLayout());

        //first card

        JPanel firstPanel = new JPanel(new GridBagLayout());
        firstPanel.setOpaque(false);

        JLabel label1 = new JLabel("", JLabel.CENTER);

        label1.setForeground(Color.RED);
        label1.setFont(new Font("Serif", Font.BOLD, 15));

        firstPanel.add(label1,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        firstCard = new CardView(cardWidth, cardHeight);
        firstPanel.add(firstCard,
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JPanel bankerPanel = new JPanel(new GridBagLayout());
        bankerPanel.setOpaque(false);

        JLabel bankerLabel = new JLabel("B", JLabel.RIGHT);
        bankerLabel.setForeground(Color.RED);
        bankerLabel.setFont(new Font("Serif", Font.BOLD, fontSize));

        bankerResult = new ResultLabel("");
        bankerPanel.add(bankerResult, new GridBagConstraints(6, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));

        bankerPanel.add(bankerLabel,
                new GridBagConstraints(5, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));

        bankerCard5 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard5,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));


        bankerCard4 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard4,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));


        bankerCard3 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard3,
                new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));

        bankerCard2 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard2,
                new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));

        bankerCard1 = new CardView(cardWidth, cardHeight);
        bankerPanel.add(bankerCard1,

                new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(bankerPanel, new GridBagConstraints(0, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        bottomPanel.add(firstPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 150), 0, 0));
        add(bottomPanel, new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        packets1 = new CardsPackets("P1", cardWidth, cardHeight);
        add(packets1, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        packets2 = new CardsPackets("P2", cardWidth, cardHeight);
        add(packets2, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        packets3 = new CardsPackets("P3", cardWidth, cardHeight);
        add(packets3, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        setOpaque(false);
    }

    CardView getCardView(BullbullPosition position) {
        switch (position) {
            case FIRSTCARD:
                return firstCard;
            case BANKER1ST:
                return bankerCard1;
            case BANKER2ST:
                return bankerCard2;
            case BANKER3ST:
                return bankerCard3;
            case BANKER4ST:
                return bankerCard4;
            case BANKER5ST:
                return bankerCard5;
            case FIRSTPLAYER1ST:
                return packets1.card1;
            case FIRSTPLAYER2ST:
                return packets1.card2;
            case FIRSTPLAYER3ST:
                return packets1.card3;
            case FIRSTPLAYER4ST:
                return packets1.card4;
            case FIRSTPLAYER5ST:
                return packets1.card5;
            case SECONDPLAYER1ST:
                return packets2.card1;
            case SECONDPLAYER2ST:
                return packets2.card2;
            case SECONDPLAYER3ST:
                return packets2.card3;
            case SECONDPLAYER4ST:
                return packets2.card4;
            case SECONDPLAYER5ST:
                return packets2.card5;
            case THIRDPLAYER1ST:
                return packets3.card1;
            case THIRDPLAYER2ST:
                return packets3.card2;
            case THIRDPLAYER3ST:
                return packets3.card3;
            case THIRDPLAYER4ST:
                return packets3.card4;
            case THIRDPLAYER5ST:
                return packets3.card5;
            default:
                return null;
        }
    }

    public void setBankerResult(String result) {
        bankerResult.setText(result);
    }

    public void setPlayer1Result(String result) {
        packets1.result.setText(result);
    }

    public void setPlayer2Result(String result) {
        packets2.result.setText(result);
    }

    public void setPlayer3Result(String result) {
        packets3.result.setText(result);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CardsPanel cardsPanel = new CardsPanel();

        cardsPanel.setBankerResult("NN");
        cardsPanel.setPlayer1Result("X");
        cardsPanel.setPlayer2Result("N6");
        cardsPanel.setPlayer3Result("N1");

        frame.getContentPane().add(cardsPanel);
//	        frame.setSize(1920, 540);
        frame.setSize(1360, 768 / 2);
        frame.pack();

        frame.setVisible(true);
//	        cardsPanel.getBankerCard1().setCardImage(GameImages.GAME_CARDS.get("AH"));


    }


    private class CardsPackets extends JPanel {
        private CardView card1, card2, card3, card4, card5;
        private JLabel result, label;

        private int cardWidth, cardHeight;
        private String labelText;

        private CardsPackets(String label, int cardWidth, int cardHeight) {
            this.cardWidth = cardWidth;
            this.cardHeight = cardHeight;
            labelText = label;

            addComponents();
            setOpaque(false);
        }

        private void addComponents() {
            setLayout(new GridBagLayout());

            JPanel panel1 = new JPanel(new GridBagLayout());
            panel1.setOpaque(false);
            card3 = new CardView(cardWidth, cardHeight);
            panel1.add(card3, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5));
            card2 = new CardView(cardWidth, cardHeight);
            panel1.add(card2, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5));
            card1 = new CardView(cardWidth, cardHeight);
            panel1.add(card1, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5));


            JPanel panel2 = new JPanel(new GridBagLayout());
            panel2.setOpaque(false);
            card5 = new CardView(cardWidth, cardHeight);
            panel2.add(card5, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5));
            card4 = new CardView(cardWidth, cardHeight);
            panel2.add(card4, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5));

            add(panel1, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            add(panel2, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            JPanel panel3 = new JPanel(new GridBagLayout());
            panel3.setOpaque(false);
            label = new JLabel(labelText, JLabel.CENTER);
            label.setForeground(Color.decode("#1195ea"));
            label.setFont(new Font("Serif", Font.BOLD, fontSize));
            panel3.add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            result = new ResultLabel("");
            panel3.add(result, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            add(panel3, new GridBagConstraints(1, 0, 1, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
    }


    private class ResultLabel extends JLabel {
        private ResultLabel(String text) {
            super(text);
            setHorizontalAlignment(JLabel.CENTER);
            setPreferredSize(new Dimension(60, Math.min(50, cardHeight)));
            setMinimumSize(new Dimension(60, Math.min(50, cardHeight)));
            setFont(new Font("Serif", Font.BOLD, fontSize));
//            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
            setForeground(Color.LIGHT_GRAY);
        }
    }
}

