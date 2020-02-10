package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.GameResultRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LocalGameResultRecordService implements GameResultRecordService {
    private static final Path LOCAL_RESULT_PATH = Paths.get("tmp", "data");

    @Override
    public void saveGameResults(GameIdentity gameIdentity, List<GameResult> gameResults) throws IOException {
        File file = LOCAL_RESULT_PATH.resolve(getResultFileName(gameIdentity)).toFile();

        if (!file.exists()) {
            FileUtils.forceMkdirParent(file);
            Files.createFile(file.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(file, false);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            for (GameResult gameResult : gameResults) {
                oos.writeObject(gameResult);
            }
            oos.flush();
        }
    }

    @Override
    public List<GameResult> loadGameResults(GameIdentity gameIdentity) {
        List<GameResult> gameResults = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(LOCAL_RESULT_PATH.resolve(getResultFileName(gameIdentity)).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            GameResult gameResult;
            while ((gameResult = (GameResult) ois.readObject()) != null) {
                gameResults.add(gameResult);
            }
            return gameResults;
        } catch (FileNotFoundException e) {
            log.warn("Result record not exist.");
        } catch (EOFException e) {
            return gameResults;
        } catch (ClassNotFoundException | IOException e) {
            log.error("Load Result record failed.");
        }

        return gameResults;
    }


    private String getResultFileName(GameIdentity gameIdentity) {
        String filename = String.format("%s-%s-result", gameIdentity.getGameCategory(), gameIdentity.getTableNumber());
        filename = Base64.getEncoder().encodeToString(filename.getBytes());
        return StringUtils.stripEnd(filename, "=") + ".dat";
    }
}
