package com.og.ogplus.dealerapp.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ScannerService {
    protected List<Listener> listenerList = new CopyOnWriteArrayList<>();

    public void startScanner() {
        startScanner(1000);
    }

    public abstract void startScanner(int safetySleepTime);

    public abstract void stopScanner();

    public abstract void writeString(String text);

    public void addListener(Listener listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listenerList.remove(listener);
    }

    public interface Listener {
        void onReceiveData(String data);
    }
}
