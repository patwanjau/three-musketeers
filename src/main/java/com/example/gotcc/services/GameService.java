package com.example.gotcc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gotcc.controllers.InvalidVoteException;
import com.example.gotcc.entity.Game;
import com.example.gotcc.entity.Game.GameState;
import com.example.gotcc.entity.Game.PlayerType;
import com.example.gotcc.entity.GameHistory;
import com.example.gotcc.entity.Player;
import com.example.gotcc.model.GameMove;
import com.example.gotcc.repository.GameHistoryRepository;
import com.example.gotcc.repository.GameRepository;
import com.example.gotcc.repository.PlayerRepository;

@Service
public class GameService {
    private final GameEngine engine = new GameEngine();
    private GameHistoryRepository playHistoryRepository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;

    @Transactional
    public void play(Game game, GameMove move) {
        int currentResult = game.getCurrentValue();
        validateMove(move, currentResult);
        if (game.isNew() || move.getValue() == 1) {
            GameState gameState = game.isNew() ? GameState.ACTIVE : GameState.FINISHED;
            game.setState(gameState);
            gameRepository.save(game);
        }
        saveGameMove(game, move);
    }

    private void validateMove(GameMove vote, int currentResult) {
        if (PlayerType.HUMAN == PlayerType.getType(vote.getInitiator())) {
            boolean isValidResult = engine.isValidPlayResult(currentResult, vote.getValue());
            if (!isValidResult) {
                throw new InvalidVoteException("Invalid move. Value incorrect");
            }
        }
    }

    private void saveGameMove(Game game, GameMove move) {
        GameHistory history = new GameHistory();
        history.setResult(move.getValue());
        history.setPlayer(PlayerType.getType(move.getInitiator()));
        history.setGame(game);
        playHistoryRepository.save(history);
        game.getHistory().add(history);
    }

    @Transactional
    public void createGame(Player player, PlayerType initiator) {
        Game game = new Game();
        game.setInitiator(initiator);
        game.setPlayer(player);
        gameRepository.save(game);
    }

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Autowired
    public void setGameHistoryRepository(GameHistoryRepository playHistoryRepository) {
        this.playHistoryRepository = playHistoryRepository;
    }
}
