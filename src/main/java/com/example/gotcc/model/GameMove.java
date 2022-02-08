package com.example.gotcc.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class GameMove {
    @NotNull
    private String player;
    @Pattern(regexp = "engine|human", message = "Please choose a valid option")
    private String initiator;
    @Min(value = 1, message = "Invalid entry ${validateValue}. Value Must be greater than or equal to {min}")
    private Integer value;

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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
