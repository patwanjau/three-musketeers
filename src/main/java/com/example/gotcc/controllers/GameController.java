package com.example.gotcc.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gotcc.entity.Game;
import com.example.gotcc.entity.Game.PlayerType;
import com.example.gotcc.entity.Game.Status;
import com.example.gotcc.entity.GameHistory;
import com.example.gotcc.entity.Player;
import com.example.gotcc.model.GameSession;
import com.example.gotcc.repository.GameHistoryRepository;
import com.example.gotcc.repository.GameRepository;
import com.example.gotcc.repository.PlayerRepository;

@Controller
@RequestMapping("/games")
public class GameController extends BaseController {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameHistoryRepository playHistoryRepository;
    private final int DIVISION_FACTOR = 3;

    public GameController(GameRepository gameRepository,
                          PlayerRepository playerRepository, GameHistoryRepository playHistoryRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playHistoryRepository = playHistoryRepository;
    }

    @GetMapping("/start")
    public String start(Model model, HttpSession session) {
        GameSession game = new GameSession();
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

    @PostMapping("/start")
    public String start(@ModelAttribute(name = "game") @Valid GameSession game, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "start-game";
        }
        Optional<Player> player = playerRepository.findByUsername(game.getPlayer());
        if (player.isEmpty()) {
            return "redirect:/users/signup";
        }
        // Create the game
        Game persistableGame = new Game();
        PlayerType initiator = PlayerType.getType(game.getInitiator());
        persistableGame.setInitiator(initiator);
        persistableGame.setPlayer(player.get());
        gameRepository.save(persistableGame);
        return "redirect:/games/play";
    }

    @GetMapping("/play")
    public String play(Model model, HttpSession session) {
        Optional<Player> player = fetchPlayerBySession(session);
        if (player.isPresent()) {
            Optional<Game> availableGame = loadAvailableGame(player.get());
            if (availableGame.isPresent()) {
                Game game = availableGame.get();
                loadGameHistory(model, game);
                if (game.getInitiator() == PlayerType.COMPUTER && game.getStatus() == Status.NEW) {
                    logger.debug("Computer Started");
                    GameSession gSession = computerPlay(player.get(), game.getStartValue());
                    vote(gSession, game, null);
                    model.addAttribute("lastPlay", gSession.getVote());
                }
                model.addAttribute("game", game);
                GameSession vote = new GameSession();
                vote.setPlayer(player.get().getUsername());
                vote.setInitiator(PlayerType.PLAYER.getType());
                model.addAttribute("vote", vote);
                return "play-game";
            }
        }
        return "redirect:/games/start";
    }

    private GameSession computerPlay(Player opponent, int playValue) {
        GameSession gSession = new GameSession();
        gSession.setInitiator(PlayerType.COMPUTER.getType());
        gSession.setPlayer(opponent.getUsername());
        int move = playValue % DIVISION_FACTOR;
        move = (move == 1 ? -1 : Math.min(move, 1));
        int vote = calculateResult(playValue, move);
        gSession.setVote(Integer.toString(vote));
        return gSession;
    }

    private List<GameHistory> loadGameHistory(Model model, Game game) {
        List<GameHistory> history = game.getHistory();
        if (game.getStatus() != Status.NEW) {
            history.sort(Comparator.comparing(GameHistory::getPlayedAt));
            GameHistory lastPlay = history.get(history.size() - 1);
            model.addAttribute("lastPlay", lastPlay.getResult());
        }
        model.addAttribute("moves", history);
        return history;
    }

    @Transactional
    public GameHistory vote(GameSession vote, Game game, Integer previousMove) {
        if (vote.getVote() != null) {
            int action = Integer.parseInt(vote.getVote());
            GameHistory history = new GameHistory();
            int playValue = previousMove != null ? previousMove : game.getStartValue();
            if (vote.getInitiator().equals(PlayerType.PLAYER.getType())) {
                GameSession matchSession = computerPlay(game.getPlayer(), playValue);
                int expected = Integer.parseInt(matchSession.getVote());
                if (expected != action) {
                    throw new InvalidVoteException("Invalid move. Value incorrect");
                }
            }
            history.setResult(action);
            if (game.getStatus() == Status.NEW && previousMove == null) {
                game.setStatus(Status.ACTIVE);
                gameRepository.save(game);
            }
            if (action == 1) {
                game.setStatus(Status.COMPLETED);
                gameRepository.save(game);
            }
            history.setPlayer(Game.PlayerType.getType(vote.getInitiator()));
            history.setGame(game);
            return playHistoryRepository.save(history);
        }
        return null;
    }

    private int calculateResult(Integer resultValue, int move) {
        int calcResult = resultValue + move;
        if (calcResult % DIVISION_FACTOR != 0) {
            throw new IllegalMoveException(move);
        }
        return calcResult / DIVISION_FACTOR;
    }

    @PostMapping("/play")
    public String play(@ModelAttribute("vote") @Valid GameSession game, Model model, BindingResult bindingResult,
                       HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "play-game";
        }
        try {
            return play(model, game, session);
        } catch (InvalidVoteException ve) {
            Optional<Player> player = fetchPlayerBySession(session);
            player.ifPresent(p ->refreshModel(model, p));
            bindingResult.rejectValue("vote", "error.gameSession", ve.getMessage());
//            model.addAttribute("vote", game);
            return "play-game";
        }
    }

    private String play(Model model, GameSession vote, HttpSession session) {
        Optional<Player> player = fetchPlayerBySession(session);
        if (player.isEmpty()) {
            return "redirect:/games/start";
        }
        Optional<Game> availableGame = loadAvailableGame(player.get());
        if (availableGame.isEmpty()) {
            return "redirect:/games/start";
        }
        Game game = availableGame.get();
        loadGameHistory(model, game);
        Integer lastPlay = (Integer) model.getAttribute("lastPlay");
        GameHistory history = vote(vote, game, lastPlay);
        if (Integer.parseInt(vote.getVote()) != 1) {
            GameSession gSession = computerPlay(player.get(), Integer.parseInt(vote.getVote()));
            if (history == null && !game.getHistory().isEmpty()) {
                history = game.getHistory().get(game.getHistory().size() - 1);
            }
            lastPlay = history != null ? history.getResult() : null;
            vote(gSession, game, lastPlay);
            vote.setVote(gSession.getVote());
        }
        refreshModel(model, player.get());
        return "play-game";
    }

    private void refreshModel(Model model, Player player) {
        Optional<Game> availableGame = loadAvailableGame(player);
        if (availableGame.isPresent()) {
            Game game = availableGame.get();
            loadGameHistory(model, game);
            model.addAttribute("game", game);
        }
    }

    private Optional<Player> fetchPlayerBySession(HttpSession session) {
        if (session.isNew()) {
            return Optional.empty();
        }
        Optional<String> sessionPlayer = Optional.ofNullable(session.getAttribute("player"))
            .map(Object::toString);
        if (sessionPlayer.isPresent()) {
            return playerRepository.findByUsername(sessionPlayer.get());
        }
        return Optional.empty();
    }

    private Optional<Game> loadAvailableGame(Player player) {
        return player.getGames().stream()
            .filter(game1 -> game1.getStatus() == Status.NEW || game1.getStatus() == Status.ACTIVE)
            .findAny();
    }
}
