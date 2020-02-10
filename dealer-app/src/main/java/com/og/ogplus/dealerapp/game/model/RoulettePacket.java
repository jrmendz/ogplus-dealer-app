package com.og.ogplus.dealerapp.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoulettePacket {

    private static final Map<Integer, String> warningMessages;

    static {
        warningMessages = new HashMap<>();
        warningMessages.put(0, "Game OK");
        warningMessages.put(1, "Exceed Preset Speed (%.1f RPM)");
        warningMessages.put(2, "Ball Launch in same direction as Wheel");
        warningMessages.put(4, "Rim Sensor Error");
        warningMessages.put(8, "Void Game or Low Ball Spin Speed");
    }

    private GameState gameState;

    private int gameNum;

    private int lastResult;

    private int warningFlag;

    private float rotorSpeed;

    private Direction wheelDirection;

    private int revolutionNum;

    private Direction ballDirection;

    public String getWarningMessage() {
        if (warningFlag == 1) {
            return String.format(warningMessages.get(warningFlag), rotorSpeed);
        } else {
            return warningMessages.get(warningFlag);
        }
    }

    public enum GameState {
        START_GAME("1"),
        PLACE_BETS("2"),
        BALL_IN_RIM("3"),
        NO_MORE_BETS("4"),
        WINNING_NUMBER("5"),
        TABLE_CLOSED("6"),
        DEALER_LOCK("7"),
        ;

        private static final Map<String, GameState> mapper =
                Arrays.stream(GameState.values()).collect(Collectors.toMap(state -> state.code, state -> state));
        String code;

        GameState(String code) {
            this.code = code;
        }

        public static GameState parse(String code) {
            return mapper.get(code);
        }
    }


    public enum Direction {
        CLOCKWISE,

        ANTI_CLOCKWISE,
        ;
    }


}
