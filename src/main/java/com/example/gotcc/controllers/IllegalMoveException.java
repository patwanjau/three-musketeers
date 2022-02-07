package com.example.gotcc.controllers;

public class IllegalMoveException extends RuntimeException {
    private int played;
    public IllegalMoveException(int played) {
        this.played = played;
    }
    public int getPlayed(){
        return played;
    }
}
