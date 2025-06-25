package com.ChessAI;

import com.ChessAI.config.EloCalculatorConfig;
import com.ChessAI.models.*;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import com.ChessAI.services.EloCalculatorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
public class EloCalculatorServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EloCalculatorService eloCalculatorService;

    @Autowired
    private EloCalculatorConfig eloConfig;

    @PersistenceContext
    private EntityManager entityManager;

    User user1 = new User("user1", "StrongPass23", "myEmail@abv.bg");
    User user2 = new User("user2", "AnotherStrongPass34", "anotherEmail@gmail.com");

    private List<Game> insertData(int whiteWins, int blackWins, int ties, GameType gameType, int usersElo, User user1, User user2) {
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
        userRepository.saveAll(List.of(user1, user2));
        List<Game> games = insertData(1, 0, 0, GameType.MULTIPLAYER, 1500, user1, user2);

        //insert some bot games to test if they will be counted in elo calculation(they shouldn't)
        insertData(10,9,8, GameType.BOT, 9999, user1, user2);

        eloCalculatorService.updateElo(games.get(0));

        // Note: updateElo above is annotated with @Modifying, because it is an update query.
        // Without @Modifying, the query will be treated as select and there will be an error.
        // @Modifying executes SQL directly, and it doesn't update the entity inside the persistence context.
        // This is why we clear the persistence context to ensure we get fresh data from the database
        //TODO: See if this call can be put at the end of updateElo() function instead of calling it here.
        entityManager.clear();

        // Note: Big swings like this are expected since techincally the winner has 100% win rate on his games.
        // After the provisional flag is dropped after n games, the elo should be stabilized.
        assertThat(userRepository.findByUsername("user1").get().getEloRating()).isEqualTo(2100);
        assertThat(userRepository.findByUsername("user2").get().getEloRating()).isEqualTo(900);
    }

    @Test
    public void testProvisionalEloCalculation2() {
        assertThat(eloConfig.getMinimumGamesForElo()).isGreaterThan(8);

        //User1 is a user with 5 wins, 2 losses, 1 ties
        //User2 is a user with 2 wins, 5 losses, 1 ties
        userRepository.saveAll(List.of(user1, user2));
        List<Game> games = insertData(5,2,1, GameType.MULTIPLAYER, 1800, user1, user2);

        //insert some bot games to test if they will be counted in elo calculation(they shouldn't)
        insertData(10,9,8, GameType.BOT, 9999, user1, user2);

        eloCalculatorService.updateElo(games.get(0));

        entityManager.clear();

        assertThat(userRepository.findByUsername("user1").get().getEloRating()).isEqualTo(2025);
        assertThat(userRepository.findByUsername("user2").get().getEloRating()).isEqualTo(1575);
    }

    @Test
    public void testIsProvisionalFlag() {
        userRepository.saveAll(List.of(user1, user2));

        assertThat(userRepository.findByUsername("user1").get().IsEloProvisional() == true);
        assertThat(userRepository.findByUsername("user2").get().IsEloProvisional() == true);

        List<Game> games = insertData(eloConfig.getMinimumGamesForElo() - 1,0,0, GameType.MULTIPLAYER, 1800, user1, user2);
        eloCalculatorService.updateElo(games.get(0));
        entityManager.clear();

        assertThat(userRepository.findByUsername("user1").get().IsEloProvisional() == true);
        assertThat(userRepository.findByUsername("user2").get().IsEloProvisional() == true);

        insertData(1,0,0,GameType.MULTIPLAYER,1800, user1, user2);
        eloCalculatorService.updateElo(games.get(0));
        entityManager.clear();

        assertThat(userRepository.findByUsername("user1").get().IsEloProvisional() == false);
        assertThat(userRepository.findByUsername("user2").get().IsEloProvisional() == false);

        insertData(5,7,8,GameType.MULTIPLAYER,1800, user1, user2);
        eloCalculatorService.updateElo(games.get(0));
        entityManager.clear();

        assertThat(userRepository.findByUsername("user1").get().IsEloProvisional() == false);
        assertThat(userRepository.findByUsername("user2").get().IsEloProvisional() == false);
    }

    private void testUpdateActualElo(int gameCount, int eloUser1, int eloUser2, int expectedEloUser1, int expectedEloUser2, String id) {
        assertThat(gameCount).isGreaterThan(0);

        User user1 = new User(id + "1", "StrongPass23", "myEmail@abv.bg");
        User user2 = new User(id + "2", "AnotherStrongPass34", "anotherEmail@gmail.com");

        //big K factor should always be used for small number of games played
        user1.setEloRating(eloUser1);
        user1.setIsEloProvisional(false);
        user2.setEloRating(eloUser2);
        user2.setIsEloProvisional(false);
        userRepository.saveAll(List.of(user1, user2));

        //Elo shouldn't depend on these games
        insertData(gameCount - 1,0,0,GameType.MULTIPLAYER,1, user1, user2);
        insertData(5, 4, 3, GameType.BOT, 1, user1, user2);

        //After this game, the big K Factor should be used
        //Elo depends only on users elo and current game result
        Game game = new Game();
        game.setUser1(user1);
        game.setUser2(user2);
        game.setUser1Color(PlayerColor.WHITE);
        game.setUser2Color(PlayerColor.BLACK);
        game.setUser1Elo(eloUser1);
        game.setUser2Elo(eloUser2);
        game.setGameType(GameType.MULTIPLAYER);
        game.setGameStatus(GameStatus.WINNER_WHITE);
        gameRepository.save(game);

        eloCalculatorService.updateElo(game);
        entityManager.clear();

        assertThat(userRepository.findByUsername(id + "1").get().getEloRating() == expectedEloUser1);
        assertThat(userRepository.findByUsername(id + "2").get().getEloRating() == expectedEloUser2);
    }

    @Test
    public void testUpdateActualEloBigKFactor() {
        //Notice not enough games have been played for mid K Factor so big K Factor is used
        testUpdateActualElo(1, 1400, 1600, 1430, 1570, "1");
        testUpdateActualElo(eloConfig.getMinGameKFactor() - 1, 1400, 1600, 1430, 1570, "2");
        testUpdateActualElo(eloConfig.getMinGameKFactor() - 1, 1234, 1234, 1254, 1214, "3");
    }

    @Test
    public void testUpdateActualEloMidKFactor() {
        //Enough games have been played for mid K Factor, but not enough elo for small K Factor
        testUpdateActualElo(eloConfig.getMinGameKFactor(), 1400, 1600, 1415, 1585, "1");
        testUpdateActualElo(eloConfig.getMinGameKFactor() + 50, 1400, 1600, 1415, 1585, "2");
        testUpdateActualElo(eloConfig.getMinGameKFactor(), 1234, 1234, 1244, 1224, "3");
    }

    @Test
    public void testUpdateActualEloSmallKFactor() {
        //Enough games have been played and elo is big enough for small K Factor
        testUpdateActualElo(eloConfig.getMinGameKFactor(), 2500, 2700, 2507, 2693, "1");
        testUpdateActualElo(eloConfig.getMinGameKFactor(), 2800, 2800, 2805, 2795, "2");
    }
}
