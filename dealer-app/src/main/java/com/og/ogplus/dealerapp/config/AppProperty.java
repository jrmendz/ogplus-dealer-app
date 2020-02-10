package com.og.ogplus.dealerapp.config;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.GameIdentity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperty {
    @NotNull
    private GameCategory gameCategory;
    @NotNull
    private String tableNumber;

    private Map<String, String> socketUrl;

    private Map<String, String> keySeparator;

    private String socketSessionPrefix;

    private String videoUrl;
    
    private String gui;

    private boolean squeezeMode;

    private boolean biddingMode;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration biddingTime = Duration.ofSeconds(10);

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration biddingExtendTime = Duration.ofSeconds(10);

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration squeezeTime1 = Duration.ofSeconds(7);

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration squeezeTime2 = Duration.ofSeconds(7);

    @Getter
    private GameIdentity gameIdentity;

    @PostConstruct
    public void init() {
        this.gameIdentity = new GameIdentity(gameCategory, tableNumber);
    }

    public String getSocketUrl(GameCategory gameCategory) {
        return socketUrl.get(gameCategory.name().toLowerCase().replaceAll("_", ""));
    }

    public String getMQTopic() {
        return String.format("dealer-app-%s-%s", gameCategory, tableNumber);
    }

}
