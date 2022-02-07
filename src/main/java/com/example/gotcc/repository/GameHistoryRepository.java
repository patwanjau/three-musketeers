package com.example.gotcc.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.gotcc.entity.GameHistory;

public interface GameHistoryRepository extends CrudRepository<GameHistory, Long> {
}
