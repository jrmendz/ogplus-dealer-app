package com.og.ogplus.dealerapp.view;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@ConditionalOnExpression("${app.gui:true}")
public class ResultDialog extends JDialog {
    private JLabel resultLabel;

    public ResultDialog(DealerAppWindowsLayout layout) {
        super(layout, false);
        setUndecorated(true);
        setFocusableWindowState(false); //make dialog won't get focus, such that dealer app can receive key event
        getRootPane().setOpaque(false);
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));

        resultLabel = new JLabel();
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setVerticalAlignment(JLabel.CENTER);
        resultLabel.setOpaque(true);
        Dimension resultLabelDimension = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 3);
        resultLabel.setPreferredSize(resultLabelDimension);
        resultLabel.setMinimumSize(resultLabelDimension);
        resultLabel.setMaximumSize(resultLabelDimension);

        add(resultLabel);
        pack();

        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }

    void showResult(String msg, Font font, Color bgColor, Color fgColor) {
        resultLabel.setText(msg);
        resultLabel.setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 220));
        resultLabel.setForeground(fgColor);
        resultLabel.setFont(font);
        setVisible(true);
    }

    void hideResult() {
        setVisible(false);
    }
}
