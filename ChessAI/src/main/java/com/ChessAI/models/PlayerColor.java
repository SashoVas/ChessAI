package com.ChessAI.models;

public enum PlayerColor {
    WHITE,
    BLACK,
    UNKNOWN;

    public static PlayerColor getOpponentColor(PlayerColor color) {
        return color == WHITE ? BLACK : WHITE;
    }

    public static PlayerColor getRandomColor() {
        return Math.random() < 0.5 ? WHITE : BLACK;
    }
}
