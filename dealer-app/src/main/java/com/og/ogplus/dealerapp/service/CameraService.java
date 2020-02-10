package com.og.ogplus.dealerapp.service;

public interface CameraService {
    void switchCamera(Mode mode);

    enum Mode {
        DEFAULT, ZOOMED, RESULT,
        ;
    }

}
