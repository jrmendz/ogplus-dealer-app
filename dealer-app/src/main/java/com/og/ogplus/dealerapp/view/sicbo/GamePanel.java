package com.og.ogplus.dealerapp.view.sicbo;

import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.dealerapp.view.GameImages;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.awt.GridBagConstraints.*;

@ConditionalOnExpression("'${app.game-category}'=='SIC_BO' && ${app.gui:true}")
@Component
public class GamePanel extends JPanel {
    public static final Map<SicBoPoint, Image> imageByPoint;

    static {
        imageByPoint = new HashMap<>();
        imageByPoint.put(SicBoPoint.POINT_1, GameImages.SIC_1);
        imageByPoint.put(SicBoPoint.POINT_2, GameImages.SIC_2);
        imageByPoint.put(SicBoPoint.POINT_3, GameImages.SIC_3);
        imageByPoint.put(SicBoPoint.POINT_4, GameImages.SIC_4);
        imageByPoint.put(SicBoPoint.POINT_5, GameImages.SIC_5);
        imageByPoint.put(SicBoPoint.POINT_6, GameImages.SIC_6);
    }

    private final int BUTTON_SIZE;
    @Getter
    private List<ButtonGroup> buttonGroups;

    public GamePanel() {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        BUTTON_SIZE = screenWidth >= 1920 ? 100 : screenWidth >= 1600 ? 85 : 60;

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(0, 2, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        buttonGroups = IntStream.range(0, 3)
                .mapToObj(index -> {
                    ButtonGroup buttonGroup = new ButtonGroup();
                    Arrays.stream(SicBoPoint.values())
                            .map(point -> {
                                JToggleButton btn = new JToggleButton();
                                btn.setActionCommand(point.name()); //binding SicBoPoint
                                btn.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                                btn.setBackground(Color.WHITE);
                                btn.setIcon(new ImageIcon(imageByPoint.get(point).getScaledInstance(BUTTON_SIZE - 5, BUTTON_SIZE - 5, Image.SCALE_DEFAULT)));
                                return btn;
                            })
                            .peek(button -> panel.add(button, new GridBagConstraints(RELATIVE, index, 1, 1, 1, 1, CENTER, BOTH, new Insets(20, 3, 20, 3), 0, 0)))
                            .forEach(buttonGroup::add);
                    return buttonGroup;
                }).collect(Collectors.toList());

        add(panel, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.add(new GamePanel());
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

        int width = BUTTON_SIZE * 6 + 500;
        int height = BUTTON_SIZE * 3 + 140;

        graphics2D.fillRoundRect((getWidth() - width) / 2, (getHeight() - height) / 2, width, height, 300, 450);
        graphics2D.setPaint(Color.decode("#263305"));
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0, width, height, 300, 450));

        graphics2D.setPaint(Color.decode("#263305"));
        width = BUTTON_SIZE * 6 + 150;
        height = BUTTON_SIZE + 20;
        graphics2D.fillRoundRect((getWidth() - width) / 2, (getHeight() - height) / 2 - BUTTON_SIZE - 40, width, height, 100, 150);
        graphics2D.fillRoundRect((getWidth() - width) / 2, (getHeight() - height) / 2, width, height, 100, 150);
        graphics2D.fillRoundRect((getWidth() - width) / 2, (getHeight() - height) / 2 + BUTTON_SIZE + 40, width, height, 100, 150);

        graphics2D.setPaint(Color.decode("#732613"));
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0 - BUTTON_SIZE - 40, width, height, 100, 150));
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0, width, height, 100, 150));
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0 + BUTTON_SIZE + 40, width, height, 100, 150));
    }
}
