package com.og.ogplus.dealerapp.service.impl;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.og.ogplus.dealerapp.exception.CommPortException;
import com.og.ogplus.dealerapp.service.ScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommScannerService extends ScannerService {

    private static final Object lock = new Object();

    private ThreadPoolTaskScheduler taskScheduler;

    private SerialPort serialPort;

    public CommScannerService(String port) throws CommPortException {
        this.serialPort = getSerialPort(port);

        if (serialPort == null) {
            String errorMsg = String.format("Can't not found scanner with port name: %s in system.", port);
            log.error(errorMsg);
            throw new CommPortException(errorMsg, port);
        } else {
            serialPort.addDataListener(new SerialPortMessageListener() {
                @Override
                public byte[] getMessageDelimiter() {
                    return "\r".getBytes();
                }

                @Override
                public boolean delimiterIndicatesEndOfMessage() {
                    return true;
                }

                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    String data = new String(event.getReceivedData()).trim();
                    log.trace("[{}]: {}", serialPort.getSystemPortName(), data);
                    listenerList.forEach(listener -> taskScheduler.execute(() -> listener.onReceiveData(data)));
                }
            });
        }
    }


    private SerialPort getSerialPort(String port) {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(serialPort -> serialPort.getSystemPortName().equals(port))
                .findAny()
                .orElse(null);
    }

    @Override
    public void startScanner(int safetySleepTime) {
        synchronized (lock) {   //not allowed parallel open port
            if (serialPort.isOpen()) {
                log.warn("Serial port({}) have opened.", serialPort.getSystemPortName());
                return;
            }

            if (serialPort.openPort(safetySleepTime)) {
                log.info("Serial port({}) opened.", serialPort.getSystemPortName());
            } else {
                log.warn("Serial port({}) open failed. ({})", serialPort.getSystemPortName(), serialPort);
            }
        }
    }

    @Override
    public void stopScanner() {
        synchronized (lock) {
            if (!serialPort.isOpen()) {
                return;
            }

            if (serialPort.closePort()) {
                log.info("Serial port({}) closed.", serialPort.getSystemPortName());
            } else {
                log.warn("Serial port({}) close failed.", serialPort.getSystemPortName());
            }
        }
    }

    @Override
    public void writeString(String text) {
        if (!serialPort.isOpen()) {
            startScanner();
        }

        byte[] buffer = text.getBytes();
        serialPort.writeBytes(buffer, buffer.length);
    }

    @Override
    public String toString() {
        return serialPort.getDescriptivePortName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommScannerService that = (CommScannerService) o;
        return Objects.equals(serialPort, that.serialPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPort);
    }

    @Autowired
    public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
