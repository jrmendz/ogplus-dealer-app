package com.og.ogplus.dealerapp.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class HintTextField extends JTextField implements FocusListener {

    private final String hint;
    private boolean showingHint;

    public HintTextField(final String hint) {
        super(hint);
        this.hint = hint;
        this.showingHint = true;
        setForeground(Color.GRAY);
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        setForeground(Color.BLACK);
        if(this.getText().isEmpty()) {
            super.setText("");
            showingHint = false;
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText(hint);
            showingHint = true;
            setForeground(Color.GRAY);
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        showingHint = false;
        setForeground(Color.BLACK);
    }

    public void clear() {
        super.setText(hint);
        showingHint = true;
        setForeground(Color.GRAY);
    }
}