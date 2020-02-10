package com.og.ogplus.dealerapp.view.moneywheel;

import com.og.ogplus.common.enums.MoneyWheelSymbol;
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
public class MoneyWheelConfirmDialog extends ConfirmDialog {

    private JLabel symbolLabel;

    public static void main(String[] args) {
        MoneyWheelConfirmDialog dialog = new MoneyWheelConfirmDialog();
        dialog.showDialog(MoneyWheelSymbol.ODDS_20);
    }

    @Override
    protected JLabel getMainComponent() {
        if (symbolLabel == null) {
            this.symbolLabel = new JLabel();
            this.symbolLabel.setPreferredSize(new Dimension(300, 300));
            this.symbolLabel.setFont(new Font("", Font.BOLD, 180));
            this.symbolLabel.setHorizontalAlignment(JLabel.CENTER);
            this.symbolLabel.setOpaque(true);
        }
        return this.symbolLabel;
    }

    public void showDialog(MoneyWheelSymbol symbol) {
        symbolLabel.setText(symbol.getReadableFormat());
        symbolLabel.setBackground(GamePanel.colors[symbol.ordinal()]);
        showDialog();
    }
}
