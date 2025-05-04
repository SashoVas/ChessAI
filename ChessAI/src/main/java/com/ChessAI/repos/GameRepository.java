package com.ChessAI.repos;

import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.GameType;
import com.ChessAI.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Set<Game> findByGameStatusAndGameType(GameStatus gameStatus, GameType gameType);

    @Query("SELECT g FROM Game g WHERE (g.user1.username = :username OR g.user2.username = :username) AND g.gameType = :gameType")
    List<Game> findByUsernameAndGameType(String username, GameType gameType);

    @Query("SELECT COUNT(g) FROM Game g WHERE (g.user1.username = :username OR g.user2.username = :username) AND g.gameType = :gameType")
    Integer findGameCount(String username, GameType gameType);

    @Transactional
    @Modifying
    @Query("UPDATE Game g SET g.gameStatus = :gameStatus WHERE g.gameId = :gameId")
    void updateGameStatusByGameId(GameStatus gameStatus, Integer gameId);

    @Query("SELECT COUNT(g) FROM Game g WHERE g.gameType = :gameType AND ((g.user1.username = :username AND g.gameStatus = 'FIRST_PLAYER_WON') OR (g.user2.username = :username AND g.gameStatus = 'SECOND_PLAYER_WON'))")
    Integer findWinCountByUsername(String username, GameType gameType);

    @Query("SELECT COUNT(g) FROM Game g WHERE g.gameStatus = 'DRAW' AND (g.user1.username = :username OR g.user2.username = :username) AND g.gameType = :gameType")
    Integer findTieCountByUsername(String username, GameType gameType);
}
