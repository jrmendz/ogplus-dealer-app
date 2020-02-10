package com.og.ogplus.dealerapp.view.threecards;

import com.og.ogplus.dealerapp.view.CardView;
import com.og.ogplus.dealerapp.view.GameImages;
import lombok.Getter;
import javax.swing.*;
import java.awt.*;

@Getter
public class ThreeCardsPanel extends JPanel {

    private final int cardWidth;
    private final int cardHeight;
    private CardView phoenixCard1;
    private CardView phoenixCard2;
    private CardView phoenixCard3;
    private CardView dragonCard1;
    private CardView dragonCard2;
    private CardView dragonCard3;
    @Getter
    private JLabel phoenixResult;
    @Getter
    private JLabel dragonResult;

    public ThreeCardsPanel() {
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

        JPanel phoenixPanel = new JPanel(new GridBagLayout());
        phoenixPanel.setOpaque(false);
        phoenixPanel.add(new JLabel(), new GridBagConstraints());
        phoenixPanel.add(new JLabel(), new GridBagConstraints());
        phoenixPanel.add(new JLabel(), new GridBagConstraints());

        phoenixResult = new JLabel("", JLabel.CENTER);
        phoenixResult.setPreferredSize(new Dimension(300, 80));
        phoenixResult.setFont(new Font("Serif", Font.BOLD, 32));
        phoenixResult.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        phoenixResult.setForeground(Color.GREEN);
        phoenixPanel.add(phoenixResult,
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JLabel phoenixLabel = new JLabel("Phoenix", JLabel.CENTER);
        phoenixLabel.setForeground(Color.RED);
        phoenixLabel.setPreferredSize(new Dimension(300, 80));
        phoenixLabel.setFont(new Font("Serif", Font.BOLD, 76));
        phoenixPanel.add(phoenixLabel,
                new GridBagConstraints(1, 1, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        phoenixCard3 = new CardView(cardWidth, cardHeight);
        phoenixPanel.add(phoenixCard3,
                new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        phoenixCard2 = new CardView(cardWidth, cardHeight);
        phoenixPanel.add(phoenixCard2,
                new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        phoenixCard1 = new CardView(cardWidth, cardHeight);
        phoenixPanel.add(phoenixCard1,
                new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));


        JPanel dragonPanel = new JPanel(new GridBagLayout());
        dragonPanel.setOpaque(false);
        dragonPanel.add(new JLabel(), new GridBagConstraints());
        dragonPanel.add(new JLabel(), new GridBagConstraints());
        dragonPanel.add(new JLabel(), new GridBagConstraints());

        JLabel dragonLabel = new JLabel("Dragon", JLabel.CENTER);
        dragonLabel.setForeground(Color.YELLOW);
        dragonLabel.setPreferredSize(new Dimension(300, 80));
        dragonLabel.setFont(new Font("Serif", Font.BOLD, 76));
        dragonPanel.add(dragonLabel,
                new GridBagConstraints(3, 1, 2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        dragonResult = new JLabel("", JLabel.CENTER);
        dragonResult.setPreferredSize(new Dimension(300, 80));
        dragonResult.setFont(new Font("Serif", Font.BOLD, 32));
        dragonResult.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        dragonResult.setForeground(Color.GREEN);
        dragonPanel.add(dragonResult,
                new GridBagConstraints(5, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        dragonCard3 = new CardView(cardWidth, cardHeight);
        dragonPanel.add(dragonCard3,
                new GridBagConstraints(3, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        dragonCard2 = new CardView(cardWidth, cardHeight);
        dragonPanel.add(dragonCard2,
                new GridBagConstraints(4, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        dragonCard1 = new CardView(cardWidth, cardHeight);
        dragonPanel.add(dragonCard1,
                new GridBagConstraints(5, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(cardMargin * 10, cardMargin, 0, cardMargin), 0, 0));

        add(phoenixPanel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(dragonPanel,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        setOpaque(false);
    }

    public static void main(String[] args){
    	 JFrame frame = new JFrame();
	        ThreeCardsPanel cardsPanel = new ThreeCardsPanel();
	        frame.add(cardsPanel);
	        frame.setSize(1920, 540);
	        frame.setVisible(true);
	        cardsPanel.getDragonCard3().setCardImage(GameImages.GAME_CARDS.get("AH"));
	        cardsPanel.getDragonCard2().setCardImage(GameImages.GAME_CARDS.get("AH"));
	        cardsPanel.getDragonCard1().setCardImage(GameImages.GAME_CARDS.get("AH"));
	        cardsPanel.getPhoenixCard1().setCardImage(GameImages.GAME_CARDS.get("AH"));
	        cardsPanel.getPhoenixCard2().setCardImage(GameImages.GAME_CARDS.get("AH"));
	        cardsPanel.getPhoenixCard3().setCardImage(GameImages.GAME_CARDS.get("AH"));
    }
}
