package com.og.ogplus.dealerapp.view.roulette;

import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.dealerapp.view.ConfirmDialog;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RouletteConfirmDialog extends ConfirmDialog {

    private JLabel slotLabel;

    public static void main(String[] args) {
        RouletteConfirmDialog dialog = new RouletteConfirmDialog();
        dialog.showDialog(RouletteSlot.SLOT_0);
    }

    @Override
    protected JLabel getMainComponent() {
        if (slotLabel == null) {
            this.slotLabel = new JLabel();
            this.slotLabel.setPreferredSize(new Dimension(300, 300));
            this.slotLabel.setFont(new Font("", Font.BOLD, 180));
            this.slotLabel.setHorizontalAlignment(JLabel.CENTER);
            this.slotLabel.setForeground(Color.WHITE);
            this.slotLabel.setOpaque(true);
        }

        return this.slotLabel;
    }

    public void showDialog(RouletteSlot slot) {
        slotLabel.setText(slot.getReadableFormat());
        slotLabel.setBackground(GamePanel.getSlotColor(slot));

        showDialog();
    }
}
