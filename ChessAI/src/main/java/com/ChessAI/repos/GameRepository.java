package com.ChessAI.repos;

import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Set<Game> findByGameStatusAndGameType(GameStatus gameStatus, GameType gameType);
}
