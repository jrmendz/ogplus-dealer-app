package com.og.ogplus.dealerapp.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("${app.gui:false}")
@Component
public abstract class DealerAppLinuxLayout implements DealerAppView {

    @Override
    public JRootPane getRootPane() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JButton getChangeDealerButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DetailsPanel getDetailsPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JToggleButton getAutoStartButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JButton getStartButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JLabel getStatusLabel() {
        // TODO Auto-generated method stub
        return null;
    }

   

    @Override
    public String getStage() {
        // TODO Auto-generated method stub
        return null;
    }

}
