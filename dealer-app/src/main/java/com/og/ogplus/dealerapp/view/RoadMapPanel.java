package com.og.ogplus.dealerapp.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class RoadMapPanel extends JPanel {

    private static final int DEFAULT_ROW_SIZE = 6;
    private static final int DEFAULT_COLUMN_SIZE = 12;

    private static final int MAXIMUM_WIDTH = 600;
    private static final int MAXIMUM_HEIGHT = 300;

    private static final int PREFER_WIDTH = 540;
    private static final int PREFER_HEIGHT = 270;

    private static final int MINIMUM_WIDTH = 480;
    private static final int MINIMUM_HEIGHT = 240;

    protected final int ROADMAP_WEIGHT_SIZE;
    protected final int ROADMAP_HEIGHT_SIZE;

    private final JLabel[][] labels;

    @Getter
    private int rowNum;
    @Getter
    private int columnNum;

    private int count = 0;

    public RoadMapPanel() {
        this(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE);
    }

    public RoadMapPanel(int row, int column) {
        this.rowNum = row;
        this.columnNum = column;

        setLayout(new GridLayout(row, column));

        int width;
        int height;
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        if (screenWidth >= 1920) {
            width = MAXIMUM_WIDTH;
            height = MAXIMUM_HEIGHT;
        } else if (screenWidth >= 1600) {
            width = PREFER_WIDTH;
            height = PREFER_HEIGHT;
        } else {
            width = MINIMUM_WIDTH;
            height = MINIMUM_HEIGHT;
        }

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        ROADMAP_WEIGHT_SIZE = width / column;
        ROADMAP_HEIGHT_SIZE = height / row;

        labels = new JLabel[row][column];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < column; ++j) {
                JLabel label = new JLabel();
                label.setSize(new Dimension(ROADMAP_WEIGHT_SIZE, ROADMAP_HEIGHT_SIZE));
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                add(label);
                labels[i][j] = label;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        RoadMapPanel panel = new RoadMapPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        panel.addRoadMap(GameImages.ROADMAP_BANKER);
    }

    public void addRoadMap(Image image) {
        count++;
        if (count > rowNum * columnNum) {
            count -= rowNum;

            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < columnNum - 1; ++j) {
                    setImage(i, j, getImage(i, j + 1));
                }
            }

            for (int i = 0; i < rowNum; ++i) {
                setImage(i, columnNum - 1, (Image) null);
            }
        }

        setImage((count - 1) % rowNum, (count - 1) / rowNum, image);
        revalidate();
        repaint();
    }

    protected void setImage(int row, int col, Image image) {
        ImageIcon imageIcon = null;
        if (image != null) {
            imageIcon = new ImageIcon(image.getScaledInstance(ROADMAP_WEIGHT_SIZE - 3, ROADMAP_HEIGHT_SIZE - 3, Image.SCALE_SMOOTH));
        }
        setImage(row, col, imageIcon);
    }

    protected void setImage(int row, int col, Icon icon) {
        labels[row][col].setIcon(icon);
    }

    protected Icon getImage(int row, int col) {
        return labels[row][col].getIcon();
    }

    public void reset() {
        count = 0;
        for (int i = 0; i < rowNum; ++i) {
            for (int j = 0; j < columnNum; ++j) {
                setImage(i, j, (Image) null);
            }
        }
        revalidate();
        repaint();
    }

}
