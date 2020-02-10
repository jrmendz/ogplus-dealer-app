package com.og.ogplus.dealerapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "card")
public class CardProperty {
    public static final String SHUFFLE_CODE = "shuffle";

    @Getter
    @Setter
    private Map<String, String> codes;

    @PostConstruct
    public void init() {
        codes = codes.keySet().stream().collect(Collectors.toMap(key -> codes.get(key), key -> key));
    }

    public Optional<String> getCardCode(String code) {
        return Optional.ofNullable(codes.get(code));
    }

}
