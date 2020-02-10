package com.og.ogplus.dealerapp.view.moneywheel;

import com.og.ogplus.common.enums.MoneyWheelSymbol;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.awt.GridBagConstraints.*;

@Getter
public class GamePanel extends JPanel {
    public static final Color[] colors = {Color.decode("#bfb234"), Color.decode("#3b8ec1"), Color.decode("#9674c2"),
            Color.decode("#568e2b"), Color.decode("#a7722d"), Color.decode("#9f4446"), Color.GRAY};

    private Map<MoneyWheelSymbol, JButton> buttonMap = new HashMap<>();

    private JLabel roundSymbolLabel;

    public GamePanel() {

        setLayout(new GridBagLayout());
        setOpaque(false);
        setMinimumSize(new Dimension(1360, 768 / 2));
        add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(4, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(5, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(6, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        JPanel roundInfoPanel = new JPanel(new GridBagLayout());
        roundInfoPanel.setOpaque(false);
        JLabel label = new JLabel("ROUND");
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("", Font.BOLD, 72));
        roundInfoPanel.add(label,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, EAST, NONE, new Insets(0, 0, 0, 0), 50, 0));

        roundSymbolLabel = new JLabel();
        roundSymbolLabel.setPreferredSize(new Dimension(550, 75));
        roundSymbolLabel.setBackground(Color.WHITE);
        roundSymbolLabel.setOpaque(true);
        roundSymbolLabel.setFont(new Font("", Font.BOLD, 60));

        roundInfoPanel.add(roundSymbolLabel,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, WEST, NONE, new Insets(0, 0, 0, 0), 50, 0));

        add(roundInfoPanel,
                new GridBagConstraints(0, 1, 7, 1, 1, 1, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 50));

        Arrays.stream(MoneyWheelSymbol.values()).forEach(symbol -> {
            JButton jButton = new JButton(symbol.getReadableFormat());
            jButton.setFont(new Font("", Font.BOLD, 72));
            jButton.setPreferredSize(new Dimension(150, 150));
            jButton.setBackground(colors[symbol.ordinal()]);
            jButton.setFocusable(false);
            buttonMap.put(symbol, jButton);
            add(jButton, new GridBagConstraints(symbol.ordinal(), 2, 1, 1, 1, 1, NORTH, NONE, new Insets(0, 0, 0, 0), 0, 0));
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new GamePanel());
        frame.setSize(1360, 768 / 2);
        frame.setVisible(true);
    }
}
