package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.service.StageRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
@Service
public class LocalStageRecordService implements StageRecordService {
    private static final Path LOCAL_STAGE_PATH = Paths.get("tmp", "data");

    @Override
    public void saveStage(GameIdentity gameIdentity, Stage stage) throws Exception {
        File file = LOCAL_STAGE_PATH.resolve(getStageFileName(gameIdentity)).toFile();
        if (!file.exists()) {
            FileUtils.forceMkdirParent(file);
            Files.createFile(file.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(file, false);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(stage);
        }
    }

    @Override
    public Stage loadStage(GameIdentity gameIdentity) throws Exception {
        try (FileInputStream fis = new FileInputStream(LOCAL_STAGE_PATH.resolve(getStageFileName(gameIdentity)).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Stage) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Stage record not exist.");
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Load stage record failed.");
        }
    }

    private String getStageFileName(GameIdentity gameIdentity) {
        String filename = String.format("%s-%s-stage", gameIdentity.getGameCategory(), gameIdentity.getTableNumber());
        filename = Base64.getEncoder().encodeToString(filename.getBytes());
        return StringUtils.stripEnd(filename, "=") + ".dat";
    }

}
