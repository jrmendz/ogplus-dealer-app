package com.og.ogplus.dealerapp.view;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.model.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class StageSettingDialog extends JDialog {
    @Getter
    private JTextField shoeTextField;
    @Getter
    private JTextField roundTextField;
//    @Getter
//    private JButton loadBtn;
    @Getter
    private JButton confirmBtn;

    private boolean isConfirm;

    public StageSettingDialog(DealerAppWindowsLayout mainFram) {
        super(mainFram, "Stage Setting", true);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(GameImages.LOGO);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 30;
        constraints.ipady = 75;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel shoeLabel = new JLabel("Shoe", JLabel.CENTER);
        shoeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(shoeLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        shoeTextField = new NumberTextField();
        shoeTextField.setPreferredSize(new Dimension(100, 50));
        shoeTextField.setFont(new Font("Helvetica", Font.BOLD, 36));
        shoeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DIVIDE:
                        roundTextField.requestFocus();
                        break;
                    case KeyEvent.VK_SUBTRACT:
                        String text = shoeTextField.getText();
                        if (text.length() > 0) {
                            shoeTextField.setText(text.substring(0, text.length() - 1));
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        confirmBtn.doClick();
                        break;
//                    case KeyEvent.VK_ADD:
//                        loadBtn.doClick();
//                        break;
                }
            }
        });
        add(shoeTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.ipadx = 30;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel roundLabel = new JLabel("Round", JLabel.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(roundLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        roundTextField = new NumberTextField();
        roundTextField.setPreferredSize(new Dimension(100, 50));
        roundTextField.setFont(new Font("Helvetica", Font.BOLD, 36));
        roundTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_MULTIPLY:
                        shoeTextField.requestFocus();
                        break;
                    case KeyEvent.VK_SUBTRACT:
                        String text = roundTextField.getText();
                        if (text.length() > 0) {
                            roundTextField.setText(text.substring(0, text.length() - 1));
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        confirmBtn.doClick();
                        break;
//                    case KeyEvent.VK_ADD:
//                        loadBtn.doClick();
//                        break;
                }
            }
        });
        add(roundTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.NORTH;
        confirmBtn = new JButton("CONFIRM");
        confirmBtn.setPreferredSize(new Dimension(200, 25));
        confirmBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure to submit your changes?", "Message", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                setVisible(false);
                dispose();
                isConfirm = true;// 确定提交stage
            }
        });

        add(confirmBtn, constraints);

        // stageSettingDialog窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isConfirm = false;// 取消提交stage
            }
        });

//        constraints = new GridBagConstraints();
//        constraints.gridx = 2;
//        constraints.gridy = 1;
//        constraints.gridwidth = 2;
//        constraints.anchor = GridBagConstraints.NORTH;
//        loadBtn = new JButton("CONTINUE");
//        loadBtn.setPreferredSize(new Dimension(100, 25));
//        add(loadBtn, constraints);

        setSize(new Dimension(500, 200));
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

    }

    public Stage getDialog() {
        setVisible(true);

        Stage stage = null;
        if (isConfirm) {
            String shoe = shoeTextField.getText().trim();
            String round = roundTextField.getText().trim();

            if (StringUtils.isBlank(round)) {
                throw new RuntimeException("Invalid stage");
            }

            stage = Stage.builder().shoe(StringUtils.isBlank(shoe) ? null : Integer.parseInt(shoe)).round(Integer.parseInt(round)).build();
        }
        return stage;
    }

    public void showDialog() {
        setVisible(true);
    }

}
