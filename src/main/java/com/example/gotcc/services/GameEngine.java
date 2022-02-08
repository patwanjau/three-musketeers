package com.example.gotcc.services;

public class GameEngine {
    public final int GAME_CONTROL_FACTOR = 3;

    public boolean isValidPlayResult(int gameResult, int currentPlay) {
        int calcResult = computeNewResult(gameResult);
        return calcResult == currentPlay;
    }

    public int computeMoveDirection(int input) {
        int move = input % GAME_CONTROL_FACTOR;
        return (move == 1 ? -1 : Math.min(move, 1));
    }

    public int computeNewResult(int input) {
        int moveDirection = computeMoveDirection(input);
        return (input + moveDirection) / GAME_CONTROL_FACTOR;
    }
}
