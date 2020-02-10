package com.og.ogplus.dealerapp.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

public abstract class ConfirmDialog extends JDialog {
    private static final int DEFAULT_WIDTH = 625;
    private static final int DEFAULT_HEIGHT = 675;

    private JLabel textLabel;

    private JButton confirmBtn;

    private JButton cancelBtn;

    @Getter
    private boolean confirm;

    public ConfirmDialog() {
        this(null, "Warning");
    }

    public ConfirmDialog(Frame owner, String title) {
        super(owner, title, true);
        setIconImage(GameImages.LOGO);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addComponent();
        addShortcut();
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelBtn.doClick();
            }
        });
    }

    private void addComponent() {
        setLayout(new GridBagLayout());

        add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(1, 0, 1, 1, 5, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(new JLabel(), new GridBagConstraints(2, 0, 1, 1, 1, 0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

        textLabel = new JLabel("", JLabel.CENTER);
        textLabel.setFont(new Font("", Font.BOLD, 36));
        add(textLabel, new GridBagConstraints(0, 1, 3, 1, 0, 0, CENTER, NONE, new Insets(30, 0, 50, 0), 0, 0));

        JComponent component = getMainComponent();
        component.setOpaque(true);
        add(component, new GridBagConstraints(1, 2, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 50, 0), 0, 0));

        confirmBtn = new JButton("CONFIRM");
        confirmBtn.setPreferredSize(new Dimension(180, 100));
        confirmBtn.setFont(new Font("", Font.BOLD, 28));
        confirmBtn.setFocusable(false);
        confirmBtn.addActionListener(e -> {
            confirm = true;
            setVisible(false);
        });

        add(confirmBtn, new GridBagConstraints(1, 3, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 0, 0, 225), 0, 0));

        cancelBtn = new JButton("CANCEL");
        cancelBtn.setPreferredSize(new Dimension(180, 100));
        cancelBtn.setFont(new Font("", Font.BOLD, 28));
        cancelBtn.setFocusable(false);
        cancelBtn.addActionListener(e -> {
            confirm = false;
            setVisible(false);
        });

        add(cancelBtn, new GridBagConstraints(1, 3, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 225, 0, 0), 0, 0));
    }

    //获取键盘快捷操作  sam
    private void addShortcut() {
        final String ACTION_CONFIRM = "CONFIRM";
        final String ACTION_CANCEL = "CANCEL";

        InputMap inputMap = getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_CONFIRM);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), ACTION_CANCEL);

        ActionMap actionMap = getRootPane().getActionMap();

        actionMap.put(ACTION_CONFIRM, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                confirmBtn.doClick();
            }
        });

        actionMap.put(ACTION_CANCEL, new AbstractAction() {
            private long lastPressTimeMillis;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastPressTimeMillis < 200) {
                    cancelBtn.doClick();
                }
                lastPressTimeMillis = System.currentTimeMillis();
            }
        });

    }

    protected abstract JComponent getMainComponent();

    protected void showDialog(String text) {
        //cancelBtn.setEnabled(true);
        textLabel.setText(text);
        confirm = false;
        setVisible(true);
    }

    protected void showDialog() {
        showDialog("Please check the result:");
    }
}
