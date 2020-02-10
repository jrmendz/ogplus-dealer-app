package com.og.ogplus.dealerapp.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerSettingDialog extends JDialog {

    private Map<String, ScannerSetting> scannerSettingMap = new HashMap<>();

    @Getter
    private Map<String, String> scannerNameByTag = new HashMap<>();

    private List<String> tagList;

    private JButton confirmBtn;

    private int row = 0;

    public ScannerSettingDialog(List<String> names, List<String> scannerNames) {
        this(null, "Scanner Setting", names, scannerNames);
    }

    public ScannerSettingDialog(Frame owner, String title, List<String> tags, List<String> scannerNames) {
        super(owner, title, true);
        this.tagList = tags;
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setIconImage(GameImages.LOGO);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmBtn.doClick();
            }
        });

        tags.stream()
                .map(name -> new ScannerSetting(name, scannerNames.toArray()))
                .peek(scannerSetting -> scannerSettingMap.put(scannerSetting.tag, scannerSetting))
                .forEach(scannerSetting -> {
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = 0;
                    constraints.gridy = row;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    constraints.weightx = 1;
                    constraints.weighty = 1;
                    constraints.ipadx = 50;
                    constraints.ipady = 20;
                    constraints.fill = GridBagConstraints.NONE;
                    constraints.anchor = GridBagConstraints.EAST;

                    JLabel label = new JLabel(scannerSetting.tag);
                    add(label, constraints);

                    constraints = new GridBagConstraints();
                    constraints.gridx = 1;
                    constraints.gridy = row;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    constraints.weightx = 2;
                    constraints.weighty = 1;
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                    constraints.anchor = GridBagConstraints.CENTER;

                    add(scannerSetting.jComboBox, constraints);

                    constraints = new GridBagConstraints();
                    constraints.gridx = 2;
                    constraints.gridy = row;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    constraints.weightx = 1;
                    constraints.weighty = 1;
                    constraints.fill = GridBagConstraints.NONE;
                    constraints.anchor = GridBagConstraints.CENTER;

                    add(scannerSetting.detectBtn, constraints);

                    row++;
                });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;

        confirmBtn = new JButton("APPLY");
        confirmBtn.addActionListener(event -> {
            setVisible(false);
            scannerSettingMap.values()
                    .forEach(scannerSetting -> {
                        scannerNameByTag.put(scannerSetting.tag, scannerSetting.jComboBox.getSelectedItem().toString());
                        scannerSetting.getDetectBtn().setSelected(false);
                    });
            dispose();
        });
        add(confirmBtn, constraints);
        confirmBtn.setFocusable(false);

        setSize(new Dimension(400, row * 50 + 50));
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD1:
                        clickDetectBtn(1);
                        break;
                    case KeyEvent.VK_NUMPAD2:
                        clickDetectBtn(2);
                        break;
                    case KeyEvent.VK_NUMPAD3:
                        clickDetectBtn(3);
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        clickDetectBtn(4);
                        break;
                    case KeyEvent.VK_NUMPAD5:
                        clickDetectBtn(5);
                        break;
                    case KeyEvent.VK_NUMPAD6:
                        clickDetectBtn(6);
                        break;
                    case KeyEvent.VK_ENTER:
                        confirmBtn.doClick();
                        break;
                }
            }
        });
    }

    private void clickDetectBtn(int index) {
        if (tagList.size() >= index) {
            ScannerSetting scannerSetting = scannerSettingMap.get(tagList.get(index - 1));
            scannerSetting.getDetectBtn().doClick();
        }
    }

    public ScannerSetting getScannerSetting(String name) {
        return scannerSettingMap.get(name);
    }

    public String getScannerNameByTag(String name) {
        return scannerNameByTag.get(name);
    }

    public static class ScannerSetting {
        private String tag;
        @Getter
        private JComboBox jComboBox;
        @Getter
        private JToggleButton detectBtn;

        public ScannerSetting(String tag, Object[] objects) {
            this.tag = tag;
            this.jComboBox = new JComboBox(objects);
            this.detectBtn = new JToggleButton("Detect");
            detectBtn.setFocusable(false);
            jComboBox.setFocusable(false);
        }
    }

}
