package com.og.ogplus.dealerapp.view.roulette;

import com.og.ogplus.common.enums.RouletteSlot;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.og.ogplus.common.enums.RouletteSlot.*;
import static java.awt.GridBagConstraints.*;

@ConditionalOnExpression("'${app.game-category}'=='ROULETTE' && ${app.gui:true}")
@Component
public class GamePanel extends JPanel {
    private static final Color ROULETTE_GREEN = Color.decode("#036705");
    private static final Color ROULETTE_RED = Color.decode("#800307");
    private static final Color ROULETTE_BLACK = Color.BLACK;

    private static final List<RouletteSlot> redSlot = Arrays.asList(SLOT_1, SLOT_3, SLOT_5, SLOT_7, SLOT_9, SLOT_12, SLOT_14, SLOT_16, SLOT_18,
            SLOT_19, SLOT_21, SLOT_23, SLOT_25, SLOT_27, SLOT_30, SLOT_32, SLOT_34, SLOT_36);

    private static final List<RouletteSlot> blackSlot = Arrays.asList(SLOT_2, SLOT_4, SLOT_6, SLOT_8, SLOT_10, SLOT_11, SLOT_13, SLOT_15, SLOT_17,
            SLOT_20, SLOT_22, SLOT_24, SLOT_26, SLOT_28, SLOT_29, SLOT_31, SLOT_33, SLOT_35);

    private static final Map<RouletteSlot, Color> colorMap;

    static {
        colorMap = new HashMap<>();
        redSlot.forEach(slot -> colorMap.put(slot, ROULETTE_RED));
        blackSlot.forEach(slot -> colorMap.put(slot, ROULETTE_BLACK));
        colorMap.put(SLOT_0, ROULETTE_GREEN);
        colorMap.put(SLOT_00, ROULETTE_GREEN);
    }

    private final int BUTTON_SIZE;
    private final Font font = new Font("", Font.BOLD, 36);
    private final Border border = BorderFactory.createLineBorder(Color.WHITE, 2);
    @Getter
    private Map<RouletteSlot, JButton> buttonMap = new HashMap<>();

    public GamePanel(@Value("${roulette.enable-00}") final boolean isSlot00enabled) {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        BUTTON_SIZE = screenWidth >= 1920 ? 100 : screenWidth >= 1600 ? 85 : 75;

        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(1360, 768 / 2));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(0, 2, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        JButton btn00 = new JButton(SLOT_00.getReadableFormat());
        buttonMap.put(RouletteSlot.SLOT_00, btn00);
        JButton btn0 = new JButton(SLOT_0.getReadableFormat());
        buttonMap.put(RouletteSlot.SLOT_0, btn0);
        if (isSlot00enabled) {
            btn00.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE * 3 / 2));
            panel.add(btn00, new GridBagConstraints(1, 0, 1, 3, 1, 1, NORTH, NONE, new Insets(3, 3, 3, 3), 0, 0));
            btn0.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE * 3 / 2));
            panel.add(btn0, new GridBagConstraints(1, 0, 1, 3, 1, 1, SOUTH, NONE, new Insets(3, 3, 3, 3), 0, 0));
        } else {
            btn0.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE * 3));
            panel.add(btn0, new GridBagConstraints(1, 0, 1, 3, 1, 1, CENTER, BOTH, new Insets(3, 3, 3, 3), 0, 0));
        }

        for (int i = 3; i >= 1; --i) {
            for (int j = 1; j <= 12; ++j) {
                RouletteSlot slot = RouletteSlot.parse(String.valueOf(i + (3 * (j - 1))));
                JButton btn = new JButton(slot.getReadableFormat());
                btn.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                panel.add(btn, new GridBagConstraints(j + 2, 3 - i, 1, 1, 1, 1, CENTER, BOTH, new Insets(3, 3, 3, 3), 0, 0));
                buttonMap.put(slot, btn);
            }
        }

        buttonMap.keySet().forEach(slot -> {
            JButton btn = buttonMap.get(slot);
            btn.setForeground(Color.WHITE);
            btn.setFont(font);
            btn.setFocusable(false);
            btn.setBorder(border);
            if (redSlot.contains(slot)) {
                btn.setBackground(ROULETTE_RED);
            } else if (blackSlot.contains(slot)) {
                btn.setBackground(ROULETTE_BLACK);
            } else {
                btn.setBackground(ROULETTE_GREEN);
            }
        });
        add(panel, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public static Color getSlotColor(RouletteSlot slot) {
        return colorMap.get(slot);
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.add(new GamePanel(true));
//        jFrame.setSize(1920, 540);
        jFrame.setSize(1360, 768 / 2);
        jFrame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D graphics2D = (Graphics2D) g;
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHints(qualityHints);
        graphics2D.setColor(Color.decode("#188856"));
        graphics2D.setStroke(new BasicStroke(5));

        int width = BUTTON_SIZE * 14 + 100;
        int height = BUTTON_SIZE * 3 + 100;

        graphics2D.fillRect((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0, width, height, 10, 10));
    }
}
