package com.og.ogplus.dealerapp.view;

import com.og.ogplus.dealerapp.service.ClientInteractService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.IntStream;

import static java.awt.GridBagConstraints.*;
import static java.awt.Image.SCALE_SMOOTH;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class LoginDialog extends JDialog {
    private static final String DEFAULT_LABEL = "Dealer Login";

    private JLabel topic;

    private JTextField inputTextField;

    private JButton cancelBtn;

    private JButton loginBtn;

    private String data = "";

    private KeyPadPanel keyPadPanel;

    public LoginDialog(DealerAppWindowsLayout frame) {
        super(frame, true);
        setUndecorated(true);
        setAlwaysOnTop(true);
        addComponents();
        addShortcut();
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }


    private void addComponents() {
        setLayout(new GridBagLayout());

        topic = new JLabel(DEFAULT_LABEL, SwingConstants.CENTER);
        topic.setFont(new Font(Font.SANS_SERIF, Font.ITALIC | Font.BOLD, 36));
        add(topic, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 20, 30));

        JPanel panel = new JPanel(new FlowLayout());
        JLabel userLabel = new JLabel(new ImageIcon(GameImages.PASSWORD_ICON.getScaledInstance(60, 60, SCALE_SMOOTH)));
        userLabel.setOpaque(false);
        panel.add(userLabel);

        inputTextField = new NumberTextField();
        inputTextField.setPreferredSize(new Dimension(300, 75));
        inputTextField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        panel.add(inputTextField);

        add(panel,
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 20, 25, 40), 0, 0));

        panel = new JPanel(new GridBagLayout());
        loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(Color.decode("#28A745"));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("", Font.BOLD, 18));
        loginBtn.setPreferredSize(new Dimension(125, 30));
        loginBtn.addActionListener(e -> {
            data = inputTextField.getText();
            setVisible(false);
            dispose();
        });
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(Color.decode("#218838"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(Color.decode("#28A745"));
            }
        });
        panel.add(loginBtn,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, EAST, NONE, new Insets(0, 0, 0, 0), 0, 0));

        cancelBtn = new JButton("CANCEL");
        cancelBtn.setBackground(Color.decode("#DC3545"));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("", Font.BOLD, 18));
        cancelBtn.setPreferredSize(new Dimension(125, 30));
        cancelBtn.addActionListener(e -> {
            data = ClientInteractService.CANCEL;
            setVisible(false);
            dispose();
        });
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(Color.decode("#C82333"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(Color.decode("#DC3545"));
            }
        });
        panel.add(cancelBtn,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, EAST, NONE, new Insets(0, 0, 0, 0), 0, 0));

        JToggleButton collapseBtn = new JToggleButton(new ImageIcon(GameImages.KEYBOARD_ICON.getScaledInstance(35, 30, SCALE_SMOOTH)));
        collapseBtn.setFocusable(false);
        collapseBtn.setPreferredSize(new Dimension(35, 30));
        collapseBtn.setOpaque(false);

        collapseBtn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                keyPadPanel.setVisible(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                keyPadPanel.setVisible(false);
            }
            pack();
            setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        });
        panel.add(collapseBtn,
                new GridBagConstraints(2, 0, 1, 1, 0, 0, WEST, NONE, new Insets(0, 20, 0, 20), 0, 0));


        add(panel,
                new GridBagConstraints(0, 2, 1, 1, 1, 1, CENTER, HORIZONTAL, new Insets(0, 50, 30, 5), 0, 0));
        keyPadPanel = new KeyPadPanel();
        add(keyPadPanel,
                new GridBagConstraints(0, 3, 1, 1, 1, 1, CENTER, NONE, new Insets(0, 0, 20, 0), 0, 0));
        keyPadPanel.setVisible(false);
    }

    private class KeyPadPanel extends JPanel {
        private KeyPadPanel() {
            setPreferredSize(new Dimension(300, 200));
            addComponent();
        }

        private void addComponent() {
            setLayout(new GridLayout(4, 3));
            Font font = new Font("Helvetica", Font.BOLD, 50);
            IntStream.of(7, 8, 9, 4, 5, 6, 1, 2, 3, 0).forEach(i -> {
                JButton button = new JButton(String.valueOf(i));
                button.setBackground(Color.decode("#6C757D"));
                button.setForeground(Color.WHITE);
                button.setFont(font);
                button.setFocusable(false);
                button.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        inputTextField.setText(inputTextField.getText() + i);
                    }
                });
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        button.setBackground(Color.decode("#5A6268"));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        button.setBackground(Color.decode("#6C757D"));
                    }
                });
                add(button);
            });

            JButton button = new JButton();
            button.setBackground(Color.decode("#6C757D"));
            button.setEnabled(false);
            add(button);

            button = new JButton(new ImageIcon(GameImages.CLEAR.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
            button.setBackground(Color.decode("#6C757D"));
            button.setFocusable(false);
            button.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = inputTextField.getText();
                    if (text.length() > 0) {
                        inputTextField.setText(text.substring(0, text.length() - 1));
                    }
                }
            });
            add(button);
        }
    }

    private void addShortcut() {
        final String ACTION_FORCE_INTERRUPT = "interrupt_login";
        final String ACTION_LOGIN = "login";
        final String ACTION_CANCEL = "cancel";
        final String ACTION_DELETE = "delete";

        InputMap inputMap = getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK), ACTION_FORCE_INTERRUPT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_LOGIN);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), ACTION_CANCEL);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), ACTION_DELETE);

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(ACTION_FORCE_INTERRUPT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                data = ClientInteractService.CANCEL;
                setVisible(false);
                dispose();
            }
        });

        actionMap.put(ACTION_LOGIN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginBtn.doClick();
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

        actionMap.put(ACTION_DELETE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputTextField.getText();
                if (text.length() > 0) {
                    inputTextField.setText(text.substring(0, text.length() - 1));
                }
            }
        });
    }

    public String showDialog() {
        return showDialog(true);
    }

    public String showDialog(String label) {
        return showDialog(label, true);
    }

    public String showDialog(boolean allowCancel) {
        return showDialog(DEFAULT_LABEL, allowCancel);
    }

    private String showDialog(String label, boolean allowCancel) {
        topic.setText(label);
        data = "";
        inputTextField.setText("");
        if (allowCancel) {
            cancelBtn.setEnabled(true);
        } else {
            cancelBtn.setEnabled(false);
        }
        setVisible(true);
        dispose();

        return data.trim();
    }

    public static void main(String[] args) {
        LoginDialog loginDialog = new LoginDialog(null);
        loginDialog.showDialog();
    }
}
