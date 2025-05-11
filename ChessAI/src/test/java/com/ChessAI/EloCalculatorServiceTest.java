package com.ChessAI;

import com.ChessAI.config.EloCalculatorConfig;
import com.ChessAI.models.*;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import com.ChessAI.services.EloCalculatorService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled //TODO: Fix tests, code/tests are incorrect
public class EloCalculatorServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EloCalculatorService eloCalculatorService;

    @Autowired
    private EloCalculatorConfig eloConfig;



    private List<Game> insertData(int whiteWins, int blackWins, int ties, GameType gameType, int usersElo) {
        User user1 = new User("user1", "StrongPass23", "myEmail@abv.bg");
        User user2 = new User("user2", "AnotherStrongPass34", "anotherEmail@gmail.com");

        userRepository.saveAll(List.of(user1, user2));

        int gameCount = whiteWins + blackWins + ties;

        List<Game> games = new ArrayList<Game>();
        for (int i = 0; i < gameCount; i++) {
            Game game = new Game();
            game.setUser1(user1);
            game.setUser2(user2);
            game.setUser1Color(PlayerColor.WHITE);
            game.setUser2Color(PlayerColor.BLACK);
            game.setUser1Elo(usersElo);
            game.setUser2Elo(usersElo);
            game.setGameType(gameType);
            if (i < whiteWins) {
                game.setGameStatus(GameStatus.WINNER_WHITE);
            } else if (i < whiteWins + blackWins) {
                game.setGameStatus(GameStatus.WINNER_BLACK);
            } else {
                game.setGameStatus(GameStatus.DRAW);
            }
            games.add(game);
        }
        gameRepository.saveAll(games);
        return games;
    }

    @Test
    public void testProvisionalEloCalculation1() {
        assertThat(eloConfig.getMinimumGamesForElo()).isGreaterThan(1);

        //User1 is a new user who just won his first game
        //User2 is a new user who just lost his first game
        List<Game> games = insertData(1, 0, 0, GameType.MULTIPLAYER, 1500);
        eloCalculatorService.updateElo(games.get(0));
        assertThat(userRepository.findByUsername("user1").get().getEloRating()).isEqualTo(1700);
        assertThat(userRepository.findByUsername("user2").get().getEloRating()).isEqualTo(1300);
    }

    @Test
    public void testProvisionalEloCalculation2() {
        assertThat(eloConfig.getMinimumGamesForElo()).isGreaterThan(8);

        //User1 is a user with 5 wins, 2 losses, 1 ties
        //User2 is a user with 2 wins, 5 losses, 1 ties
        List<Game> games = insertData(5,2,1, GameType.MULTIPLAYER, 1800);
        eloCalculatorService.updateElo(games.get(0));
        assertThat(userRepository.findByUsername("user1").get().getEloRating()).isEqualTo(2100);
        assertThat(userRepository.findByUsername("user2").get().getEloRating()).isEqualTo(1500);
    }
}
