package com.example.gotcc.controllers;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.gotcc.model.Player;

@Controller
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger("GameOfThreeLogger");

    @GetMapping({"/", "/users/signup"})
    public String index(Model model, HttpSession session) {
        if (!session.isNew()) {
            Optional<String> sessionUser = Optional.ofNullable((String) session.getAttribute("player"));
            if (sessionUser.isPresent()) {
                logger.debug("A session exists for user: {}", session.getAttribute("player"));
                return "redirect:/games/start";
            }
        }
        model.addAttribute("player", new Player());
        return "index";
    }
}
