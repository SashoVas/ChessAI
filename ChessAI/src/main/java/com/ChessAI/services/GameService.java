package com.ChessAI.services;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.GameType;
import com.ChessAI.models.PlayerColor;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public Game createGame(CreateGameDTO createGameDTO, UserDetails userDetails) {
        Game game = new Game();

        //Since user is authorized, we know that user exists
        game.setUser1(userRepository.findByUsername(userDetails.getUsername()).get());

        PlayerColor u1Color = PlayerColor.getRandomColor();
        game.setUser1Color(u1Color);
        game.setUser2Color(PlayerColor.getOpponentColor(u1Color));

        game.setGameType(createGameDTO.getGameType());
        game.setUser1TimeLeft(createGameDTO.getGameTimeSeconds());
        if (game.getGameType() == GameType.MULTIPLAYER) {
            game.setUser2TimeLeft(createGameDTO.getGameTimeSeconds());
        }

        game.setGameStatus(GameStatus.NOT_STARTED);
        game.setGameTimeSeconds(createGameDTO.getGameTimeSeconds());

        return gameRepository.save(game);
    }

    public Set<Game> getFreeRooms() {
        return gameRepository.findByGameStatusAndGameType(GameStatus.NOT_STARTED, GameType.MULTIPLAYER);
    }
}
