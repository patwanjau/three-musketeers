package com.example.gotcc.converters;

import org.springframework.core.convert.converter.Converter;

import com.example.gotcc.model.Player;

public class PlayerModelConverter implements Converter<Player, com.example.gotcc.entity.Player> {

    @Override
    public com.example.gotcc.entity.Player convert(Player source) {
        com.example.gotcc.entity.Player player = new com.example.gotcc.entity.Player();
        player.setFullname(source.getFullname());
        player.setUsername(source.getUsername());
        return player;
    }

}
