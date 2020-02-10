package com.og.ogplus.dealerapp.view;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@Slf4j
public class LoadingDialog extends JDialog {

    public LoadingDialog() {
        super((Frame) null, "", false);
        setUndecorated(true);
//        setAlwaysOnTop(true);

        JLabel label = new JLabel();
        label.setOpaque(false);
        try {
            label.setIcon(new ImageIcon(new ClassPathResource("static/images/loading.gif").getURL()));
            getRootPane().setOpaque(false);
            getContentPane().setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setText("Loading...");
            label.setFont(new Font("", Font.ITALIC, 36));
            label.setPreferredSize(new Dimension(200, 100));
        }
        add(label);
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }


    public static void main(String[] args) {
        new LoadingDialog().setVisible(true);
    }
}
