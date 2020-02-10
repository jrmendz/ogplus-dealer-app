package com.og.ogplus.dealerapp.game.model;

public class RoulettePacketParser {

    public static RoulettePacket parse(String str) throws IllegalArgumentException {
        if (!str.contains("*X;")) {
            throw new IllegalArgumentException();
        }

        String[] arr = str.split(";");
        if (arr.length < 7) {
            throw new IllegalArgumentException();
        }

        RoulettePacket.RoulettePacketBuilder builder = RoulettePacket.builder()
                .gameState(RoulettePacket.GameState.parse(arr[1]))
                .gameNum(Integer.parseInt(arr[2]))
                .lastResult(Integer.parseInt(arr[3].trim()))
                .warningFlag(Integer.parseInt(arr[4]))
                .rotorSpeed(Float.parseFloat(arr[5]) / 10)
                .wheelDirection(parseDirection(arr[6]));

        if (arr.length > 7) {
            builder.revolutionNum(Integer.parseInt(arr[8], 16));
            builder.ballDirection(parseDirection(arr[9]));
        }

        return builder.build();
    }


    private static RoulettePacket.Direction parseDirection(String s) {
        switch (s) {
            case "0":
                return RoulettePacket.Direction.CLOCKWISE;
            case "1":
                return RoulettePacket.Direction.ANTI_CLOCKWISE;
            default:
                return null;
        }
    }

}
