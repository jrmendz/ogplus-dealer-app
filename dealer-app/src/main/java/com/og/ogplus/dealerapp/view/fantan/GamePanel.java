package com.og.ogplus.dealerapp.view.fantan;

import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.dealerapp.view.GameImages;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.IntStream;

import static java.awt.GridBagConstraints.*;

@Getter
public class GamePanel extends JPanel {
    public static final Color[] COLORS = {Color.decode("#46A3FF"), Color.decode("#FF5151"), Color.decode("#46A3FF"), Color.decode("#FF5151")};

    private Map<FanTanSymbol, JButton> buttonMap = new HashMap<>();

    private final int BUTTON_WIDTH_SIZE;
    private final int BUTTON_HEIGHT_SIZE;

    public GamePanel() {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        BUTTON_WIDTH_SIZE = screenWidth >= 1920 ? 500 : screenWidth >= 1600 ? 450 : 400;
        BUTTON_HEIGHT_SIZE = screenHeight >= 1080 ? 100 : screenHeight >= 900 ? 85 : 75;

        setLayout(new GridBagLayout());
        setOpaque(false);
//        setMinimumSize(new Dimension(1360, 768 / 2));

        JButton jButton1 = new JButton("1");
        jButton1.setFont(new Font("", Font.BOLD, 50));
        jButton1.setForeground(Color.WHITE);
        jButton1.setHorizontalAlignment(JButton.CENTER);
        jButton1.setVerticalTextPosition(JButton.BOTTOM);
        jButton1.setPreferredSize(new Dimension(BUTTON_WIDTH_SIZE, BUTTON_HEIGHT_SIZE));
        jButton1.setBackground(Color.decode("#263305"));
        jButton1.setFocusable(false);
        jButton1.setIcon(new ImageIcon(createImage(1)));
        buttonMap.put(FanTanSymbol.FT_1, jButton1);

        JButton jButton2 = new JButton("2");
        jButton2.setFont(new Font("", Font.BOLD, 50));
        jButton2.setForeground(Color.WHITE);
        jButton2.setHorizontalAlignment(JButton.CENTER);
        jButton2.setVerticalTextPosition(JButton.BOTTOM);
        jButton2.setPreferredSize(new Dimension(BUTTON_WIDTH_SIZE, BUTTON_HEIGHT_SIZE));
        jButton2.setBackground(Color.decode("#263305"));
        jButton2.setFocusable(false);
        jButton2.setIcon(new ImageIcon(createImage(2)));
        buttonMap.put(FanTanSymbol.FT_2, jButton2);

        JButton jButton3 = new JButton("3");
        jButton3.setFont(new Font("", Font.BOLD, 50));
        jButton3.setForeground(Color.WHITE);
        jButton3.setHorizontalAlignment(JButton.CENTER);
        jButton3.setVerticalTextPosition(JButton.BOTTOM);
        jButton3.setPreferredSize(new Dimension(BUTTON_WIDTH_SIZE, BUTTON_HEIGHT_SIZE));
        jButton3.setBackground(Color.decode("#263305"));
        jButton3.setFocusable(false);
        jButton3.setIcon(new ImageIcon(createImage(3)));
        buttonMap.put(FanTanSymbol.FT_3, jButton3);

        JButton jButton4 = new JButton("4");
        jButton4.setFont(new Font("", Font.BOLD, 50));
        jButton4.setForeground(Color.WHITE);
        jButton4.setHorizontalAlignment(JButton.CENTER);
        jButton4.setVerticalTextPosition(JButton.BOTTOM);
        jButton4.setPreferredSize(new Dimension(BUTTON_WIDTH_SIZE, BUTTON_HEIGHT_SIZE));
        jButton4.setBackground(Color.decode("#263305"));
        jButton4.setFocusable(false);
        jButton4.setIcon(new ImageIcon(createImage(4)));
        buttonMap.put(FanTanSymbol.FT_4, jButton4);

        add(jButton1, new GridBagConstraints(0, 0, 1, 1, 1, 1, SOUTHEAST, NONE, new Insets(0, 20, 20, 0), 0, 0));
        add(jButton2, new GridBagConstraints(1, 0, 1, 1, 1, 1, SOUTHWEST, NONE, new Insets(0, 20, 20, 0), 0, 0));
        add(jButton3, new GridBagConstraints(0, 1, 1, 1, 1, 1, NORTHEAST, NONE, new Insets(0, 20, 20, 0), 0, 0));
        add(jButton4, new GridBagConstraints(1, 1, 1, 1, 1, 1, NORTHWEST, NONE, new Insets(0, 20, 20, 0), 0, 0));
    }

    private Image createImage(int index) {
        int imageSize = 60;
        int padding = 2;

        Image image = GameImages.FANTAN.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);

        BufferedImage bufferedImage = new BufferedImage(imageSize * 4 + padding * 5, imageSize + padding * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();

        IntStream.range(0, index).forEach(i -> {
            if (i == 0) {
                graphics.drawImage(image, padding, padding, null);
            } else {
                graphics.drawImage(image, padding * (i + 1) + imageSize * i, padding, null);
            }
        });

        return bufferedImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D graphics2D = (Graphics2D) g;
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHints(qualityHints);
        graphics2D.setColor(Color.decode("#188856"));
        graphics2D.setStroke(new BasicStroke(5));

        int width = BUTTON_HEIGHT_SIZE * 6 + 500;
        int height = BUTTON_HEIGHT_SIZE * 3 + 100;

        graphics2D.fillRoundRect((getWidth() - width) / 2, (getHeight() - height) / 2, width + 20, height - 20, 300, 450);
        graphics2D.setPaint(Color.decode("#263305"));
        graphics2D.draw(new RoundRectangle2D.Double((getWidth() - width) / 2.0, (getHeight() - height) / 2.0, width + 20, height - 20, 300, 450));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new GamePanel());
        frame.setSize(1360, 768 / 2);
        frame.setVisible(true);
    }
}
