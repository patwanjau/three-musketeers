package com.example.gotcc.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.gotcc.entity.Player;
import com.example.gotcc.repository.PlayerRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void whenGetIndexPageReturnSignUpPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(xpath("//input[@name=\"username\"]").exists())
            .andExpect(xpath("//input[@name=\"fullname\"]").exists())
            .andExpect(xpath("//form[@method=\"post\" and @action=\"/users/signup\"]").exists());
    }

    @Test
    public void whenSubmitSignUpRequestPlayerCreated() throws Exception {
        MultiValueMap<String, String> signupParameter = new LinkedMultiValueMap<>();
        signupParameter.add("username", "testUser1");
        signupParameter.add("fullname", "Test User1");
        mockMvc.perform(post("/users/signup")
                .params(signupParameter))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/games/start"));
        Optional<Player> player = playerRepository.findByUsername(signupParameter.getFirst("username"));
        Assertions.assertTrue(player.isPresent());
        player.ifPresent(p -> Assertions.assertEquals(signupParameter.getFirst("fullname"), p.getFullname()));
    }
}
