package com.example.gotcc.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class GameEngineTest {
    private GameEngine gameEngine;

    @BeforeEach
    public void createEngine(){
        this.gameEngine = new GameEngine();
    }

    @ParameterizedTest
    @ValueSource(ints = {56, 57, 58})
    public void testExpectedResultMatchesForProvidedInput(int playResult) {
        int expectedResult = 19;
        int result = gameEngine.computeNewResult(playResult);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void whenNotDivisibleByThreeAndIsModEqualToOneExpectNegativeOne() {
        int runValue = 58;
        int expectedResult = -1;
        int result = gameEngine.computeMoveDirection(runValue);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void whenDivisibleByThreeExpectZero() {
        int runValue = 57;
        int expectedResult = 0;
        int result = gameEngine.computeMoveDirection(runValue);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void whenNotDivisibleByThreeAndIsModGreaterThanOneExpectOne() {
        int runValue = 56;
        int expectedResult = 1;
        int result = gameEngine.computeMoveDirection(runValue);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testCurrentPlayValueMatchesExpectedPlay(){
        int gameResult = Integer.MAX_VALUE;
        int currentPlay = gameEngine.computeNewResult(gameResult);
        Assertions.assertTrue(gameEngine.isValidPlayResult(gameResult, currentPlay));
    }

    @Test
    public void testCurrentPlayValueFailsMatchForExpectedPlay(){
        int gameResult = Integer.MAX_VALUE;
        int currentPlay = gameEngine.computeNewResult(gameResult) + 1;
        Assertions.assertFalse(gameEngine.isValidPlayResult(gameResult, currentPlay));
    }
}
