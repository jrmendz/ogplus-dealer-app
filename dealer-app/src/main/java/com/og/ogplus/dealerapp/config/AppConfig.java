package com.og.ogplus.dealerapp.config;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.dealerapp.service.GameService;
import com.og.ogplus.dealerapp.view.DealerAppSettingDialog;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.File;
import java.util.Optional;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:cards.properties")
})
public class AppConfig {

    public static final String ERROR_INFO = "Table not exists.";

    @Bean
    public Table table(GameService gameService, AppProperty appProperty) {
        Optional<Table> optionalTable = gameService.getTable(appProperty.getGameCategory(), appProperty.getTableNumber());
        if (optionalTable.isPresent()) {
            return optionalTable.get();
        } else {
            File file = new File(DealerAppSettingDialog.CONFIG_FILENAME);
            file.delete();
            throw new RuntimeException(ERROR_INFO);
        }
    }

    @Primary
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);

        return taskScheduler;
    }

    @Profile("!slim")
    @Bean
    public NewTopic newTopic(AppProperty appProperty) {
        return new NewTopic(appProperty.getMQTopic(), 1, (short) 1);
    }

}
