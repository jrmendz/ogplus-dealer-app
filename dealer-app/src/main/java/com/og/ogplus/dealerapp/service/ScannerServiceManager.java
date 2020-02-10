package com.og.ogplus.dealerapp.service;

import com.fazecast.jSerialComm.SerialPort;
import com.og.ogplus.dealerapp.service.impl.CommScannerService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScannerServiceManager {

    private Map<SerialPort, ScannerService> scannerServices = new HashMap<>();

    public ScannerServiceManager(ApplicationContext applicationContext) {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        if (serialPorts.length > 0) {
            scannerServices.putAll(Arrays.stream(SerialPort.getCommPorts())
                    .collect(Collectors.toMap(serialPort -> serialPort,
                            serialPort -> applicationContext.getBean(CommScannerService.class, serialPort.getSystemPortName()))));
        }
    }

    public Optional<ScannerService> getScannerService(String portName) {
        return scannerServices.keySet().stream()
                .filter(serialPort -> serialPort.getSystemPortName().equals(portName))
                .map(scannerServices::get).findAny();
    }

    public List<ScannerService> getScannerServicesContainName(String name) {
        return scannerServices.keySet().stream()
                .filter(serialPort -> serialPort.getDescriptivePortName().toLowerCase().contains(name.toLowerCase()))
                .map(scannerServices::get).collect(Collectors.toList());
    }

    public List<ScannerService> getAllScannerServices() {
        return new ArrayList<>(scannerServices.values());
    }

    @PreDestroy
    public void destroy() {
        scannerServices.values().forEach(ScannerService::stopScanner);
    }
}
