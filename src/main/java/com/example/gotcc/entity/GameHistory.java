package com.example.gotcc.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "game_history")
public class GameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(targetEntity = Game.class, optional = false)
    private Game game;
    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;
    @Column(name = "player", nullable = false)
    @Enumerated
    private Game.PlayerType player;
    @Column(name = "result")
    private Integer result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public Game.PlayerType getPlayer() {
        return player;
    }

    public void setPlayer(Game.PlayerType player) {
        this.player = player;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer played) {
        this.result = played;
    }

    @PrePersist
    private void setPlayedAt() {
        playedAt = LocalDateTime.now();
    }
}
