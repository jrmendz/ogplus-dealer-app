package com.og.ogplus.dealerapp.component;

import com.og.ogplus.dealerapp.exception.UnexpectedScannerSettingException;
import com.og.ogplus.dealerapp.service.ScannerService;
import com.og.ogplus.dealerapp.service.ScannerServiceManager;
import com.og.ogplus.dealerapp.view.ScannerSettingDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScannerSettingHelper {

    private ScannerServiceManager scannerServiceManager;

    public int getScannerCount() {
        return getBarcodeScanners().size();
    }

    public <T> Map<T, ScannerService> getScannerServices(List<T> names) throws UnexpectedScannerSettingException {
        List<ScannerService> scannerServices = getBarcodeScanners();

        scannerServices.forEach(scannerService -> scannerService.startScanner(0));

        ScannerSettingDialog scannerSettingDialog = new ScannerSettingDialog(names.stream().map(Objects::toString).collect(Collectors.toList()),
                scannerServices.stream().map(Objects::toString).collect(Collectors.toList()));

        names.stream()
                .map(Objects::toString)
                .forEach(name -> {
                    ScannerSettingDialog.ScannerSetting scannerSetting = scannerSettingDialog.getScannerSetting(name);
                    scannerSetting.getDetectBtn().addItemListener(new ScannerSettingBtnListener(scannerSetting));
                });
        Map<String, String> scannerNameByTag = loadScannerSetting();
        scannerNameByTag.keySet()
                .forEach(tag -> {
                    ScannerSettingDialog.ScannerSetting scannerSetting;
                    if ((scannerSetting = scannerSettingDialog.getScannerSetting(tag)) != null) {
                        scannerSetting.getJComboBox().setSelectedItem(scannerNameByTag.get(tag));
                    }
                });

        scannerSettingDialog.setVisible(true);
//        scannerServices.forEach(ScannerService::stopScanner);
        try {
            Map<T, ScannerService> result = new HashMap<>();
            for (T name : names) {
                String scannerName = scannerSettingDialog.getScannerNameByTag(name.toString());
                result.put(name, scannerServices.stream()
                        .filter(scannerService -> scannerService.toString().equals(scannerName))
                        .findAny().orElseThrow(UnexpectedScannerSettingException::new));
            }
            return result;
        } finally {
            saveScannerSetting(scannerSettingDialog.getScannerNameByTag());
        }
    }

    private void saveScannerSetting(Map<String, String> scannerNameByTag) {

        StringBuilder sb = new StringBuilder();
        scannerNameByTag.keySet().forEach(tag -> sb.append(tag).append("=").append(scannerNameByTag.get(tag)).append("\n"));

        File file = new File("scanner.config");
        try {
            FileUtils.writeStringToFile(file, sb.toString(), Charset.forName("utf-8"));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }

    private Map<String, String> loadScannerSetting() {
        File file = new File("scanner.config");
        if (file.exists()) {
            try {
                return FileUtils.readLines(file, Charset.forName("utf-8")).stream()
                        .filter(StringUtils::isNotBlank)
                        .map(s -> s.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return new HashMap<>();
    }

    private List<ScannerService> getBarcodeScanners() {
        return scannerServiceManager.getScannerServicesContainName("scanner");
    }

    @Autowired
    public void setScannerServiceManager(ScannerServiceManager scannerServiceManager) {
        this.scannerServiceManager = scannerServiceManager;
    }

    private class ScannerSettingBtnListener implements ItemListener {
        private Map<ScannerService, ScannerService.Listener> listenerMap;

        private ScannerSettingBtnListener(ScannerSettingDialog.ScannerSetting scannerSetting) {
            listenerMap = getBarcodeScanners().stream()
                    .collect(Collectors.toMap(scannerService -> scannerService, scannerService -> (ScannerService.Listener) data -> {
                        scannerSetting.getJComboBox().setSelectedItem(scannerService.toString());
                        scannerSetting.getDetectBtn().doClick();
                        scannerSetting.getJComboBox().repaint();
                    }));
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            int state = e.getStateChange();

            if (state == ItemEvent.SELECTED) {
                getBarcodeScanners().forEach(scannerService -> scannerService.addListener(listenerMap.get(scannerService)));
            } else {
                getBarcodeScanners().forEach(scannerService -> scannerService.removeListener(listenerMap.get(scannerService)));
            }
        }
    }
}
