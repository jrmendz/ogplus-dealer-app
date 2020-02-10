package com.og.ogplus.dealerapp.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoPanel extends JPanel {

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 450;

    private int width;
    private int height;

    private Image im;

    public VideoPanel() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public VideoPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(400, 300));
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int x = 0;
        int y = 0;
        if (im != null) {
            x = (this.getWidth() - im.getWidth(null)) / 2;
            y = (this.getHeight() - im.getHeight(null)) / 2;
        }
        g2d.drawImage(im, x, y, this);
    }

    public void display(BufferedImage image) {
        if (image != null) {
            float scale = Math.min((float) width / image.getWidth(), (float) height / image.getHeight());
            this.im = image.getScaledInstance((int) (image.getWidth() * scale), (int) (image.getHeight() * scale), Image.SCALE_FAST);
        }
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        BufferedImage bufferedImage = new BufferedImage(800,800, BufferedImage.TYPE_INT_BGR);

        JFrame jFrame = new JFrame();
        VideoPanel videoPanel = new VideoPanel();
        jFrame.setBounds(200,200,700, 700);
        jFrame.add(videoPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

        while (true) {
            videoPanel.display(bufferedImage);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
