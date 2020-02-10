package com.og.ogplus.dealerapp.view;

import lombok.Getter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

@Component
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
public class DetailsPanel extends JPanel {

    private static final int FONT_SIZE;
    private static Font font;

    static {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        FONT_SIZE = screenWidth >= 1920 ? 42 : screenWidth >= 1600 ? 36 : 30;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(new ClassPathResource("static/fonts/Roboto Mono Medium Nerd Font Complete.ttf").getInputStream()));
            font = font.deriveFont(Font.BOLD, FONT_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("", Font.BOLD, FONT_SIZE);
        }
    }

//    private int rowNum = 0;

    @Getter
    private JLabel tableNumber;
    @Getter
    private JLabel tableStage;
    @Getter
    private JLabel dealerName;
    @Getter
    private JButton changeStageButton;
    @Getter
    private JButton shuffleButton;
    @Getter
    private JButton changeDeckButton;
    @Getter
    private JButton changeDealerButton;
    
    public DetailsPanel() {
        super(new GridBagLayout());
        addComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(450, 200));
        setMinimumSize(new Dimension(400, 150));
    }

    private void addComponents() {
        JLabel label = new JLabel("No.", JLabel.LEFT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        label.setForeground(Color.decode("#FFF7EB"));
        add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        tableNumber = new JLabel();
        tableNumber.setHorizontalAlignment(SwingConstants.LEFT);
        tableNumber.setFont(font);
        tableNumber.setForeground(Color.decode("#FFF7EB"));
        add(tableNumber, new GridBagConstraints(1, 0, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 20, 0, 0), 0, 0));

        label = new JLabel("Stage", JLabel.LEFT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        label.setForeground(Color.decode("#FFF7EB"));
        add(label, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        tableStage = new JLabel("N/A");
        tableStage.setHorizontalAlignment(SwingConstants.LEFT);
        tableStage.setFont(font);
        tableStage.setForeground(Color.decode("#FFF7EB"));
        add(tableStage, new GridBagConstraints(1, 1, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 20, 0, 0), 0, 0));

        changeStageButton = new JButton("Setting");
        changeStageButton.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        changeStageButton.setPreferredSize(new Dimension(150, 40));
        changeStageButton.setMinimumSize(new Dimension(150, 30));
        changeStageButton.setFocusable(false);
        changeStageButton.setBackground(Color.decode("#3c3f41"));
        // Deprecated
        changeStageButton.setVisible(false);
        changeStageButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                changeStageButton.setBackground(Color.decode("#6c757d"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                changeStageButton.setBackground(Color.decode("#3c3f41"));
            }
        });
        add(changeStageButton, new GridBagConstraints(2, 1, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 10, 0, 0), 0, 0));

        shuffleButton = new JButton("Shuffle");
        shuffleButton.setVisible(false);
        shuffleButton.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        shuffleButton.setPreferredSize(new Dimension(80, 40));
        shuffleButton.setMinimumSize(new Dimension(80, 30));
        shuffleButton.setFocusable(false);
        shuffleButton.setBackground(Color.decode("#3c3f41"));
        shuffleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                shuffleButton.setBackground(Color.decode("#6c757d"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                shuffleButton.setBackground(Color.decode("#3c3f41"));
            }
        });
        add(shuffleButton, new GridBagConstraints(2, 1, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        
        
        changeDeckButton = new JButton("ChangeDeck");
        changeDeckButton.setVisible(true);
        changeDeckButton.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        changeDeckButton.setPreferredSize(new Dimension(110, 40));
        changeDeckButton.setMinimumSize(new Dimension(80, 30));
        changeDeckButton.setFocusable(false);
        changeDeckButton.setBackground(Color.decode("#3c3f41"));
        changeDeckButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
            	changeDeckButton.setBackground(Color.decode("#6c757d"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	changeDeckButton.setBackground(Color.decode("#3c3f41"));
            }
        });
        add(changeDeckButton, new GridBagConstraints(2, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, -120, 0, 0), 0, 0));
        
        changeDealerButton = new JButton("ChangeDealer");
        changeDealerButton.setVisible(true);
        changeDealerButton.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        changeDealerButton.setPreferredSize(new Dimension(115, 40));
        changeDealerButton.setMinimumSize(new Dimension(80, 30));
        changeDealerButton.setFocusable(false);
        changeDealerButton.setBackground(Color.decode("#3c3f41"));
        changeDealerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
            	changeDealerButton.setBackground(Color.decode("#6c757d"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	changeDealerButton.setBackground(Color.decode("#3c3f41"));
            }
        });
        add(changeDealerButton, new GridBagConstraints(2, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 120, 0, 0), 0, 0));
        

        label = new JLabel("Dealer", JLabel.LEFT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        label.setForeground(Color.decode("#FFF7EB"));
        add(label, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        dealerName = new JLabel();
        dealerName.setHorizontalAlignment(SwingConstants.LEFT);
        dealerName.setFont(font);
        dealerName.setForeground(Color.decode("#FFF7EB"));
        add(dealerName, new GridBagConstraints(1, 2, 2, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 20, 0, 0), 0, 0));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        DetailsPanel detailsPanel = new DetailsPanel();
        frame.add(detailsPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
