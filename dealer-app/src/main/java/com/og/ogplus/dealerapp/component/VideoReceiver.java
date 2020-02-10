package com.og.ogplus.dealerapp.component;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.MAX_PRIORITY;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
public class VideoReceiver implements AbstractGame.GameListener {
    private Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();

    private FFmpegFrameGrabber grabber;

    private AppProperty appProperty;

    private DealerAppWindowsLayout dealerAppLayout;

    private AtomicBoolean restart = new AtomicBoolean(false);

    public VideoReceiver(AppProperty appProperty, DealerAppWindowsLayout dealerAppLayout) {
        this.appProperty = appProperty;
        this.dealerAppLayout = dealerAppLayout;
    }

    @Override
    public void onGameInitialized(Table table) {
        String videoUrl;
        if (StringUtils.isBlank(appProperty.getVideoUrl())) {
            videoUrl = table.getMeta().getVideoUrl();
        } else {
            videoUrl = appProperty.getVideoUrl();
        }

        if (StringUtils.isBlank(videoUrl)) {
            log.warn("video url is blank");
            return;
        }

        log.info("play video: {}", videoUrl);
        grabber = new FFmpegFrameGrabber(videoUrl);

        Thread thread = new Thread(() -> {
            do {
                try {
                    grabber.start();
                    Frame frame;
                    while (!restart.get() && (frame = grabber.grabImage()) != null) {
                        BufferedImage image = java2DFrameConverter.getBufferedImage(frame);
                        dealerAppLayout.showVideoImage(image);
                    }
                } catch (FrameGrabber.Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                } finally {
                    try {
                        grabber.stop();
                    } catch (FrameGrabber.Exception e) {
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                    dealerAppLayout.showVideoImage(null);
                    dealerAppLayout.setVideoLoadingVisible(true);
                }

                if (restart.get()) {
                    restart.set(false);
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            } while (true);
        });
        thread.setPriority(MAX_PRIORITY);
        thread.setName("video-receiver");
        thread.start();
    }


    public void refresh() {
        restart.set(true);
    }


    @PreDestroy
    public void destroy() {
        if (grabber != null) {
            try {
                grabber.release();
            } catch (FrameGrabber.Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
