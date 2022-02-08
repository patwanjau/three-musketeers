package com.example.gotcc.controllers;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gotcc.entity.Game;
import com.example.gotcc.entity.Game.GameState;
import com.example.gotcc.entity.Game.PlayerType;
import com.example.gotcc.entity.Player;
import com.example.gotcc.model.GameMove;
import com.example.gotcc.repository.PlayerRepository;
import com.example.gotcc.services.GameEngine;
import com.example.gotcc.services.GameService;

@Controller
@RequestMapping("/games")
public class GameController extends BaseController {
    private final GameService gameService;
    private final GameEngine engine = new GameEngine();
    private final String sessionKey = "player";

    @Autowired
    private PlayerRepository playerRepository;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public String start(Model model, HttpSession session) {
        GameMove game = new GameMove();
        Optional<Player> player = fetchPlayerBySession(session);
        if (player.isPresent()) {
            if (loadAvailableGame(player.get()).isPresent()) {
                return "redirect:/games/play";
            }
        }
        player.ifPresent(value -> game.setPlayer(value.getUsername()));
        model.addAttribute("game", game);
        return "start-game";
    }

    private Optional<Player> fetchPlayerBySession(HttpSession session) {
        if (session.isNew()) {
            return Optional.empty();
        }
        Optional<String> sessionPlayer = Optional.ofNullable(session.getAttribute(sessionKey))
            .map(Object::toString);
        if (sessionPlayer.isPresent()) {
            return playerRepository.findByUsername(sessionPlayer.get());
        }
        return Optional.empty();
    }

    private Optional<Game> loadAvailableGame(Player player) {
        return player.getGames().stream()
            .filter(game1 -> GameState.NEW == game1.getState() || GameState.ACTIVE == game1.getState())
            .findAny();
    }

    @PostMapping("/start")
    public String start(@ModelAttribute(name = "game") @Valid GameMove game,
                        BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "start-game";
        }
        Optional<Player> player = playerRepository.findByUsername(game.getPlayer());
        if (player.isEmpty()) {
            return "redirect:/users/signup";
        }
        PlayerType initiator = PlayerType.getType(game.getInitiator());
        gameService.createGame(player.get(), initiator);
        if (session.getAttribute(sessionKey) == null) {
            session.setAttribute(sessionKey, game.getPlayer());
        }
        return "redirect:/games/play";
    }

    @GetMapping("/play")
    public String play(Model model, HttpSession session) {
        Optional<Game> availableGame = fetchPlayerBySession(session).flatMap(this::loadAvailableGame);
        if (availableGame.isPresent()) {
            Game game = availableGame.get();
            if (GameState.NEW == game.getState() && PlayerType.COMPUTER == game.getInitiator()) {
                playComputerMove(game, game.getStartValue());
            }
            refreshModel(model, game, null);
            return "play-game";
        }
        return "redirect:/games/start";
    }

    private void playComputerMove(Game game, int currentValue) {
        GameMove move = generateComputerMove(game.getPlayer(), currentValue);
        gameService.play(game, move);
    }

    public GameMove generateComputerMove(Player opponent, int playValue) {
        GameMove move = new GameMove();
        move.setInitiator(PlayerType.COMPUTER.getType());
        move.setPlayer(opponent.getUsername());
        int result = engine.computeNewResult(playValue);
        move.setValue(result);
        return move;
    }

    private void refreshModel(Model model, Game game, GameMove gameMove) {
        if (game.isNew()) {
            game.setCurrentValue(game.getStartValue());
        }
        model.addAttribute("game", game);
        if (gameMove == null) {
            gameMove = new GameMove();
            gameMove.setPlayer(game.getPlayer().getUsername());
            gameMove.setInitiator(PlayerType.HUMAN.getType());
            model.addAttribute("move", gameMove);
        }
    }


    @PostMapping("/play")
    public String play(@ModelAttribute("move") @Valid GameMove move, Model model,
                       BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "play-game";
        }
        Optional<Game> availableGame = fetchPlayerBySession(session).flatMap(this::loadAvailableGame);
        if (availableGame.isEmpty()) {
            return "redirect:/games/start";
        }
        Game game = availableGame.get();
        try {
            play(game, move);
        } catch (InvalidVoteException ve) {
            bindingResult.rejectValue("value", "error.gameMove", ve.getMessage());
        }
        refreshModel(model, game, move);
        return "play-game";
    }

    private void play(Game game, GameMove move) {
        gameService.play(game, move);
        if (move.getValue() != 1) {
            playComputerMove(game, move.getValue());
        }
        move.setValue(null);
    }
}
