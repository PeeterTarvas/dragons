package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.mapper.GameStateMapper;
import com.bigbank.dragons.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final MugloarClient client;
    private final GameStateMapper gameStateMapper;

    @Override
    public GameState start() {
        return gameStateMapper.toEntity(client.startGame());
    }
}
