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
import java.util.Optional;
import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    Set<Game> findByGameStatusAndGameType(GameStatus gameStatus, GameType gameType);

    Optional<Game> findByGameId(Integer id);

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.moves m WHERE g.gameId = :id")
    Optional<Game> findByGameIdWithMoves(Integer id); //Solves n+1 problem

    @Query("SELECT g FROM Game g LEFT JOIN g.user1 u1 LEFT JOIN g.user2 u2 WHERE (u1.username = :username OR u2.username = :username) AND g.gameType = :gameType")
    List<Game> findByUsernameAndGameType(String username, GameType gameType);

    @Query("SELECT COUNT(g) FROM Game g LEFT JOIN g.user1 u1 LEFT JOIN g.user2 u2 WHERE (u1.username = :username OR u2.username = :username) AND g.gameType = :gameType")
    Integer findGameCount(String username, GameType gameType);
    @Query("SELECT g FROM Game g LEFT JOIN g.user1 u1 LEFT JOIN g.user2 u2 WHERE (u1.username = :username OR u2.username = :username) AND g.gameStatus = :gameStatus")

    List<Game> findByUsernameAndGameStatus(String username, GameStatus gameStatus);
}
