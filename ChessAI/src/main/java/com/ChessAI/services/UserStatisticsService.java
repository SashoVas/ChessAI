package com.ChessAI.services;

import com.ChessAI.dto.LeaderboardUserDTO;
import com.ChessAI.dto.UserStatisticsDTO;
import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.User;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserStatisticsService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public UserStatisticsDTO calculateUserStatistics(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Game> userGames = gameRepository.findAllByUsername(username);
        
        // Filter for finished games only
        List<Game> finishedGames = userGames.stream()
                .filter(game -> game.getGameStatus() == GameStatus.WINNER_WHITE || 
                               game.getGameStatus() == GameStatus.WINNER_BLACK || 
                               game.getGameStatus() == GameStatus.DRAW)
                .toList();
        
        int wins = 0, losses = 0, draws = 0;
        Game lastFinishedGame = null;
        String lastGameResult = null;
        String lastGameOpponent = null;
        
        for (Game game : finishedGames) {
            boolean isUser1 = game.getUser1().getUsername().equals(username);
            String opponent = isUser1 ? game.getUser2().getUsername() : game.getUser1().getUsername();
            
            if (game.getGameStatus() == GameStatus.DRAW) {
                draws++;
                if (lastFinishedGame == null || game.getGameId() > lastFinishedGame.getGameId()) {
                    lastFinishedGame = game;
                    lastGameResult = "Draw";
                    lastGameOpponent = opponent;
                }
            } else if (
                (game.getGameStatus() == GameStatus.WINNER_WHITE && 
                 ((isUser1 && game.getUser1Color().toString().equals("WHITE")) || 
                  (!isUser1 && game.getUser2Color().toString().equals("WHITE")))) ||
                (game.getGameStatus() == GameStatus.WINNER_BLACK && 
                 ((isUser1 && game.getUser1Color().toString().equals("BLACK")) || 
                  (!isUser1 && game.getUser2Color().toString().equals("BLACK"))))
            ) {
                wins++;
                if (lastFinishedGame == null || game.getGameId() > lastFinishedGame.getGameId()) {
                    lastFinishedGame = game;
                    lastGameResult = "Win";
                    lastGameOpponent = opponent;
                }
            } else {
                losses++;
                if (lastFinishedGame == null || game.getGameId() > lastFinishedGame.getGameId()) {
                    lastFinishedGame = game;
                    lastGameResult = "Loss";
                    lastGameOpponent = opponent;
                }
            }
        }
        
        UserStatisticsDTO result = new UserStatisticsDTO(
            user.getUsername(),
            user.getEloRating(),
            user.IsEloProvisional(),
            finishedGames.size(),
            wins,
            losses,
            draws,
            lastFinishedGame != null ? lastFinishedGame.getGameId() : null,
            lastGameResult,
            lastGameOpponent
        );
        
        return result;
    }

    public List<LeaderboardUserDTO> getTopUsers(int limit) {
        List<User> users = userRepository.findTopUsersByElo(limit);
        return users.stream()
                .map(LeaderboardUserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 