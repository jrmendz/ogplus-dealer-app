package com.og.ogplus.dealerapp.view;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
public class DateTimePanel extends JPanel {

    private JLabel dateTimeLabel;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final int FONT_SIZE;

    static {
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        FONT_SIZE = screenWidth >= 1920 ? 52 : screenWidth >= 1600 ? 46 : 40;
    }

    public DateTimePanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        dateTimeLabel.setForeground(Color.decode("#FFF7EB"));
        add(dateTimeLabel, constraints);

        Timer timer = new Timer(1000, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateTimeLabel.setText(LocalDateTime.now().format(dtf));
            }
        });
        timer.setRepeats(true);
        timer.start();

        setOpaque(false);
    }
}
