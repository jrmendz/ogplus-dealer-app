package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.dealerapp.service.CameraService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "camera.enabled=true",
        "camera.server.port=" + CameraServiceImplTest.TEST_SOCKET_SERVER_PORT,
        "camera.mode.default=" + CameraServiceImplTest.TEST_DEFAULT_MESSAGE
})
public class CameraServiceImplTest {
    static final int TEST_SOCKET_SERVER_PORT = 23456;
    static final String TEST_DEFAULT_MESSAGE = "Test";

    @SpyBean
    private CameraServiceImpl cameraService;

    @Test
    public void switchCameraToDefault() throws IOException {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(os);

        doReturn(socket).when(cameraService).createSocket();
        cameraService.switchCamera(CameraService.Mode.DEFAULT);

        Assert.assertArrayEquals(TEST_DEFAULT_MESSAGE.getBytes(), os.toByteArray());
    }

}