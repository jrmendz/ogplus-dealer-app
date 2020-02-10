package com.og.ogplus.dealerapp.view.sicbo;

import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.dealerapp.view.ConfirmDialog;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SicBoConfirmDialog extends ConfirmDialog {
    private final int LABEL_SIZE = 150;

    private JPanel panel;

    private JLabel[] diceLabelArr;

    public static void main(String[] args) {
        SicBoConfirmDialog dialog = new SicBoConfirmDialog();
        dialog.showDialog(Arrays.asList(SicBoPoint.POINT_1, SicBoPoint.POINT_2, SicBoPoint.POINT_3));
    }

    @Override
    protected JComponent getMainComponent() {
        if (panel == null) {
            diceLabelArr = new JLabel[3];
            panel = new JPanel(new GridBagLayout());
            panel.setPreferredSize(new Dimension(600, 300));

            IntStream.range(0, diceLabelArr.length)
                    .forEach(index -> {
                        diceLabelArr[index] = new JLabel();
                        diceLabelArr[index].setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
                        diceLabelArr[index].setOpaque(true);
                        panel.add(diceLabelArr[index],
                                new GridBagConstraints(index, 0, 1, 1, 1, 1, CENTER, NONE, new Insets(30, 30, 30, 30), 0, 0));
                    });
        }

        return this.panel;
    }

    public void showDialog(List<SicBoPoint> points) {
        if (points.size() != 3) {
            throw new IllegalArgumentException("Sic Bo points size should be 3.");
        }

        points.sort(Comparator.naturalOrder());
        IntStream.range(0, points.size())
                .forEach(index -> {
                    Image diceImage = GamePanel.imageByPoint.get(points.get(index));
                    diceLabelArr[index].setIcon(new ImageIcon(diceImage.getScaledInstance(LABEL_SIZE - 10, LABEL_SIZE - 10, Image.SCALE_DEFAULT)));
                });

        showDialog();
    }
}
