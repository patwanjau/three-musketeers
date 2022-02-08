package com.example.gotcc.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.gotcc.entity.Game;
import com.example.gotcc.entity.GameHistory;

public interface GameHistoryRepository extends CrudRepository<GameHistory, Long> {

//    @Query("SELECT gh FROM GameHistory gh WHERE gh.game = ?1 ORDER BY gh.id DESC")
    List<GameHistory> findAllByGameOrderByIdDesc(Game game);

    GameHistory findTopByGameOrderByIdDesc(Game game);
}
