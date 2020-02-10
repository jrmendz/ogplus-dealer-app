package com.og.ogplus.dealerapp.view;

import static java.awt.Image.SCALE_SMOOTH;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.GameIdentity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DealerAppSettingDialog extends JDialog {
	
	private GameCategory gameCategory;
	private String tableNumber;
	private JComboBox<GameCategorySelectItem> gameCategoryJComboBox;
	private JTextField tableNumberTextField;
	public final static String CONFIG_FILENAME="settings.config";

	private DealerAppSettingDialog() {
		super((Frame) null, "", true);

		setUndecorated(true);
		addComponents();
		setAlwaysOnTop(true);
		pack();
		getContentPane().requestFocusInWindow();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
	}

	private void addComponents() {
		setLayout(new GridBagLayout());

		JLabel label = new JLabel("Dealer App Settings", SwingConstants.CENTER);
		label.setForeground(Color.RED);
		label.setFont(new Font("", Font.BOLD | Font.ITALIC, 24));
		label.setPreferredSize(new Dimension(300, 60));
		add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(20, 0, 20, 0), 0, 0));

		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setOpaque(true);
		mainPanel.setBackground(Color.ORANGE);

		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.ORANGE);
		panel.setOpaque(true);
		JLabel gameLabel = new JLabel(new ImageIcon(GameImages.POKER.getScaledInstance(30, 30, SCALE_SMOOTH)));
		panel.add(gameLabel);

		gameCategoryJComboBox = new JComboBox<>();
		gameCategoryJComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				gameCategory = ((GameCategorySelectItem) e.getItem()).getGameCategory();
			}
		});
		gameCategoryJComboBox.setFont(new Font("", Font.BOLD, 18));
		gameCategoryJComboBox.setPreferredSize(new Dimension(300, 40));
		Arrays.stream(GameCategory.values()).map(GameCategorySelectItem::new).forEach(gameCategoryJComboBox::addItem);
		panel.add(gameCategoryJComboBox);

		mainPanel.add(panel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 20, 25, 40), 0, 0));

		panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.ORANGE);
		panel.setOpaque(true);
		JLabel tableLabel = new JLabel(new ImageIcon(GameImages.POKER_TABLE.getScaledInstance(30, 30, SCALE_SMOOTH)));
		panel.add(tableLabel);

		tableNumberTextField = new HintTextField("Table Number");
		tableNumberTextField.setPreferredSize(new Dimension(300, 40));
		tableNumberTextField.setFont(new Font("", Font.BOLD, 18));
		panel.add(tableNumberTextField);

		mainPanel.add(panel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 20, 25, 40), 0, 0));

		add(mainPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		JButton jButton = new JButton("Confirm");
		jButton.setFont(new Font("", Font.BOLD, 18));
		jButton.setBackground(Color.BLACK);
		jButton.setForeground(Color.WHITE);
		jButton.addActionListener(e -> {
			if (StringUtils.isBlank(tableNumberTextField.getText())) {
				JOptionPane.showMessageDialog(this, "Table Number should not be Empty", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				tableNumber = tableNumberTextField.getText().trim();
				this.setVisible(false);
			}
		});
		jButton.setPreferredSize(new Dimension(300, 35));
		add(jButton, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(20, 10, 40, 10), 0, 0));

		InputMap inputMap = getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "action_confirm");
		getRootPane().getActionMap().put("action_confirm", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton.doClick();
			}
		});
	}

	private void updateSettings(GameIdentity gameIdentity) {
		gameCategoryJComboBox.setSelectedItem(new GameCategorySelectItem(gameIdentity.getGameCategory()));
		tableNumberTextField.setText(gameIdentity.getTableNumber());
	}

	@EqualsAndHashCode
	private class GameCategorySelectItem {
		@Getter
		private GameCategory gameCategory;

		private GameCategorySelectItem(GameCategory gameCategory) {
			this.gameCategory = gameCategory;
		}

		@Override
		public String toString() {
			return gameCategory.name().replaceAll("_", " ");
		}
	}


	public static GameIdentity showDialog() {
		DealerAppSettingDialog dealerAppSettingDialog = new DealerAppSettingDialog();
		GameIdentity gameIdentity = loadSettings();
		if (gameIdentity != null) {
			dealerAppSettingDialog.updateSettings(gameIdentity);
		}
		dealerAppSettingDialog.setVisible(true);
		gameIdentity = new GameIdentity(dealerAppSettingDialog.gameCategory, dealerAppSettingDialog.tableNumber);
		saveSettings(gameIdentity);
		return gameIdentity;
	}

    public static GameIdentity loadSettings() {
		try {
			File file = new File(CONFIG_FILENAME);
			if (file.exists()) {
				String str = FileUtils.readFileToString(file, Charset.forName("utf-8"));
				if (StringUtils.isNoneBlank(str)) {
					String[] strArr = str.split(",");
					if (strArr.length != 2) {
						throw new RuntimeException("Unexpected format in " + "'" + CONFIG_FILENAME + "'");
					} else {
						GameCategory gameCategory = findCategoryByStr(strArr[0]);
						if (gameCategory != null) {
							return new GameIdentity(gameCategory, strArr[1]);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public static GameCategory findCategoryByStr(String categoryName) {
		for (GameCategory category : GameCategory.values()) {
			if (category.name().equals(categoryName)) {
				return category;
			}
		}
		return null;
	}
	
	private static void saveSettings(GameIdentity gameIdentity) {
		if (gameIdentity == null || gameIdentity.getGameCategory() == null
				|| StringUtils.isBlank(gameIdentity.getTableNumber())) {
			return;
		}

		try {
			FileUtils.writeStringToFile(new File(CONFIG_FILENAME),
					String.format("%s,%s", gameIdentity.getGameCategory(), gameIdentity.getTableNumber()),
					Charset.forName("utf-8"));
		} catch (IOException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void main(String[] args) {
		DealerAppSettingDialog.showDialog();
	}
}
