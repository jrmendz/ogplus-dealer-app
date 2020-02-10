package com.og.ogplus.dealerapp.view;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.*;

public class NumberTextField extends JTextField {

    public NumberTextField() {

        PlainDocument document = (PlainDocument) getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getText(0, doc.getLength()));
                sb.delete(offset, offset + length);

                if (sb.toString().length() == 0 || StringUtils.isNumeric(sb.toString())) {
                    super.remove(fb, offset, length);
                } else {
                    // warn the user and don't allow the insert
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getText(0, doc.getLength()));
                sb.insert(offset, string);

                if (StringUtils.isNumeric(sb.toString())) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    // warn the user and don't allow the insert
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getText(0, doc.getLength()));
                sb.replace(offset, offset + length, text);

                if (sb.toString().length() == 0 || (sb.length() <= 8 && StringUtils.isNumeric(sb.toString()))) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    // warn the user and don't allow the insert
                }

            }

        });


    }

}
