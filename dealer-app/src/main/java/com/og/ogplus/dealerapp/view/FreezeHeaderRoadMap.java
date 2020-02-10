package com.og.ogplus.dealerapp.view;

import java.awt.*;

public class FreezeHeaderRoadMap extends RoadMapPanel {

    private int count = 0;

    public FreezeHeaderRoadMap(Image[] rowHeaders, int column) {
        super(rowHeaders.length, column + 1);

        for (int r = 0; r < rowHeaders.length; r++) {
            setImage(r, 0, rowHeaders[r]);
        }
    }

    @Override
    public void addRoadMap(Image image) {
        count++;
        if (count > getRowNum() * (getColumnNum() - 1)) {
            for (int r = 0; r < getRowNum(); ++r) {
                for (int c = 1; c < getColumnNum() - 1; ++c) {
                    setImage(r, c, getImage(r, c + 1));
                }
                setImage(r, getColumnNum() - 1, (Image) null);
            }
            count -= getRowNum();
        }

        setImage((count - 1) % getRowNum(), 1 + ((count - 1) / getRowNum()), image);
        revalidate();
        repaint();
    }

    @Override
    public void reset() {
        count = 0;
        for (int r = 0; r < getRowNum(); ++r) {
            for (int c = 1; c < getColumnNum(); ++c) {
                setImage(r, c, (Image) null);
            }
        }
        revalidate();
        repaint();
    }
}
