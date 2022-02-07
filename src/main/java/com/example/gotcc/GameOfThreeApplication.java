package com.example.gotcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.gotcc.converters.PlayerModelConverter;

@SpringBootApplication
public class GameOfThreeApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(GameOfThreeApplication.class, args);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PlayerModelConverter());
    }
}
