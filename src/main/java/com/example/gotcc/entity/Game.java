package com.example.gotcc.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    @ManyToOne(targetEntity = Player.class)
    private Player player;
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "completed_at")
    private LocalDateTime completeDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column(name = "initiator")
    private PlayerType initiator;
    @Column(name = "start_value", nullable = false)
    private Integer startValue;
    @OneToMany(mappedBy = "game")
    private List<GameHistory> history;

    @PrePersist
    public void prePersist() {
        Random rand = new Random();
        int generated = rand.nextInt(300);
        startValue = generated < 100 ? (generated + 100) : generated;
        startDate = LocalDateTime.now();
        status = Status.NEW;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(LocalDateTime completeDate) {
        this.completeDate = completeDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PlayerType getInitiator() {
        return initiator;
    }

    public void setInitiator(PlayerType initiator) {
        this.initiator = initiator;
    }

    public Integer getStartValue() {
        return startValue;
    }

    public void setStartValue(Integer startValue) {
        this.startValue = startValue;
    }

    public List<GameHistory> getHistory() {
        return history;
    }

    public void setHistory(List<GameHistory> history) {
        this.history = history;
    }

    public enum Status {
        NEW, ACTIVE, COMPLETED, TERMINATED
    }

    public enum PlayerType {
        COMPUTER("engine"), PLAYER("human");
        private final String type;

        PlayerType(String type) {
            this.type = type;
        }

        public static PlayerType getType(String type) {
            for (PlayerType t : PlayerType.values()) {
                if (t.type.equals(type)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("PlayerType is invalid");
        }

        public final String getType() {
            return this.type;
        }
    }
}
