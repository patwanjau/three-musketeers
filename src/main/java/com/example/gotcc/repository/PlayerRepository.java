package com.example.gotcc.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.gotcc.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
}
