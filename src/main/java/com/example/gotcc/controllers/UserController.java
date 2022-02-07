package com.example.gotcc.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gotcc.model.Player;
import com.example.gotcc.repository.PlayerRepository;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping("/signup")
    public String createAccount(@ModelAttribute @Valid Player player, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        Optional<com.example.gotcc.entity.Player> existingPlayer = playerRepository.findByUsername(player.getUsername());
        if (existingPlayer.isPresent()) {
            bindingResult.rejectValue("username", "error.player", "Username not available");
            return "index";
        }
        com.example.gotcc.entity.Player newPlayer = conversionService.convert(player, com.example.gotcc.entity.Player.class);
        newPlayer.setRegistrationDate(LocalDateTime.now());
        playerRepository.save(newPlayer);
        session.setAttribute("player", player.getUsername());
        return "redirect:/games/start";
    }
}
