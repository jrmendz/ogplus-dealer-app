package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.dealerapp.service.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Profile({"test", "prod"})
public class CameraServiceImpl implements CameraService {

    @Value("${camera.server.host:localhost}")
    private String cameraServerHost;

    @Value("${camera.server.port:1111}")
    private int cameraServerPort;

    @Value("${camera.enabled}")
    private boolean cameraSwitchEnabled;

    @Value("${camera.mode.default:DefaultCamera}")
    private String defaultMode;

    @Value("${camera.mode.zoomed:ZoomedCamera}")
    private String zoomedMode;

    @Value("${camera.mode.result:ResultCamera}")
    private String resultMode;

    private Map<CameraService.Mode, String> modeMapper;

    private Socket socket;

    @Override
    public void switchCamera(CameraService.Mode mode) {
        if (!cameraSwitchEnabled) {
            return;
        }

        String message = modeMapper.get(mode);
        sendMessage(message, true);
    }

    private synchronized void sendMessage(String message, boolean isRetry) {
        if (socket == null || socket.isClosed()) {
            try {
                socket = createSocket();
            } catch (IOException e) {
                log.error("Create socket to camera server failed. {}", ExceptionUtils.getStackTrace(e));
                return;
            }
        }

        try {
            OutputStream os = socket.getOutputStream();
            if (StringUtils.isNotBlank(message)) {
                os.write(message.getBytes());
                os.flush();
                log.info("Send ({}) to camera server.", message);
            }
        } catch (IOException e) {
            //handle when socket disconnected by server.
            log.error("Send message({}) out failed. {}", message, ExceptionUtils.getStackTrace(e));
            try {
                socket.close();
            } catch (IOException ex) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            if (isRetry) {
                sendMessage(message, false);
            }
        }
    }


    @PostConstruct
    public void init() {
        modeMapper = new HashMap<>();
        modeMapper.put(Mode.DEFAULT, defaultMode);
        modeMapper.put(Mode.ZOOMED, zoomedMode);
        modeMapper.put(Mode.RESULT, resultMode);

        if (cameraSwitchEnabled) {
            log.info("\n[Camera Server]\ncamera.server.host: {}\ncamera.server.port: {}", cameraServerHost, cameraServerPort);
        }
    }

    Socket createSocket() throws IOException {
        Socket socket = new Socket(cameraServerHost, cameraServerPort);
        socket.setKeepAlive(true);
        return socket;
    }
}
