package com.og.ogplus.dealerapp.view.fantan;

import com.og.ogplus.common.enums.FanTanSymbol;
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
public class FanTanConfirmDialog extends ConfirmDialog {

    private JLabel symbolLabel;

    public static void main(String[] args) {
        FanTanConfirmDialog dialog = new FanTanConfirmDialog();
        dialog.showDialog(FanTanSymbol.FT_1);
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

    public void showDialog(FanTanSymbol symbol) {
        symbolLabel.setText(symbol.getReadableFormat());
        symbolLabel.setBackground(GamePanel.COLORS[symbol.ordinal()]);
        showDialog();
    }
}
