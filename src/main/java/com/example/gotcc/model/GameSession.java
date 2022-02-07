package com.example.gotcc.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class GameSession {
    @NotNull
    private String player;
    @Pattern(regexp = "engine|human", message = "Please choose a valid option")
    private String initiator;
    @Pattern(regexp = "-?\\d+", message = "Invalid entry. Must be a numeric value")
    private String vote;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
