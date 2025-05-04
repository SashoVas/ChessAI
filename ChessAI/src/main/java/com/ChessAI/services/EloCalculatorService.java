package com.ChessAI.services;

import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.GameType;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ChessAI.models.User;

import java.util.ArrayList;
import java.util.List;

/*
1.) Elo formula for players >= n games: (n is minimumGamesForElo)

Delta=K*(S-E) - this is the change in elo rating after a game
where K is K-factor:
	--40 for players <30 games
	--20 for players <2400 elo
	--10 for players >2400 elo
where S is score:
  --S=1 for win
  --S=0.5 for draw
  --S=0 for loss
where E is expected score:
E=1/(1+10^((R1 - R0)/400)), where R1 is opponent elo, R0 is player elo

2.) Elo formula for players with 0 games:
R = assumedAverageEloForNewUser = 1500

3.) Elo formula for players, who have played n > n_current > 0 games:
    R = AvgOppRating + 800(S - n_current/2)/n_current
where S is sum of the scores of the games played, given by the same formula as above
Note: This is provisional elo rating, and is only used for players with less than n games
due to the fact that the player has not played enough games to have a stable rating.
*/

@Service
public class EloCalculatorService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    //FIDE ELO rating system constants
    public static final Integer minimumGamesForElo = 5;
    public static final Integer minGameKFactor = 30;
    public static final Integer bigKFactor = 40;
    public static final Integer midKFactor = 20;
    public static final Integer smallKFactor = 10;

    private List<Integer> getOtherUsersElo(User currentUser) {
        List<Game> games = gameRepository.findByUsernameAndGameType(currentUser.getUsername(), GameType.MULTIPLAYER);
        List<Integer> eloList = new ArrayList<>();
        Integer opponentElo;

        for (Game game : games) {
            if (!game.getUser1().getUsername().equals(currentUser.getUsername())) {
                opponentElo = game.getUser1().getEloRating();
            }
            else {
                opponentElo = game.getUser2().getEloRating();
            }
            eloList.add(opponentElo);
        }

        return eloList;
    }

    private void updateProvisionalElo(User user) {
        List<Integer> elos = getOtherUsersElo(user);
        int gameCount = elos.size();

        int winCount = gameRepository.findWinCountByUsername(user.getUsername(), GameType.MULTIPLAYER);
        int tieCount = gameRepository.findTieCountByUsername(user.getUsername(), GameType.MULTIPLAYER);

        double scoreSum = winCount + (double) tieCount / 2;
        double provisionalElo = elos.stream().reduce(0, Integer::sum) / (double)gameCount;
        provisionalElo +=  800 * (scoreSum - gameCount / 2.0) / gameCount;

        userRepository.updateEloByUsername((int)provisionalElo, user.getUsername());
    }

    private int getEloKFactor(String username) {
        Integer gameCount = gameRepository.findGameCount(username, GameType.MULTIPLAYER);
        if (gameCount < minGameKFactor) {
            return bigKFactor;
        }
        Integer elo = userRepository.getEloByUsername(username);
        if (elo < 2400) {
            return midKFactor;
        }
        return smallKFactor;
    }

    private void updateActualElo(User user, User opponent,  double score) {
        int K_factor = getEloKFactor(user.getUsername());
        int userElo = user.getEloRating();
        int opponentElo = opponent.getEloRating();

        double expectedScore = 1.0 / (1 + Math.pow(10, (opponentElo - userElo) / 400.0));
        int updatedElo = (int) (userElo + K_factor * (score - expectedScore));

        userRepository.updateEloByUsername(updatedElo, user.getUsername());
    }

    public void updateElo(Integer roomId, GameStatus gameStatus) {
        Game currentGame = gameRepository.findById(roomId).orElseThrow();
        if (currentGame.getGameType() == GameType.BOT) {
            return; //No elo calculation for bot games
        }
        User user1 = currentGame.getUser1();
        User user2 = currentGame.getUser2();

        int player1Games = gameRepository.findGameCount(user1.getUsername(), GameType.MULTIPLAYER);
        int player2Games = gameRepository.findGameCount(user2.getUsername(), GameType.MULTIPLAYER);

        if (player1Games < minimumGamesForElo) {
            updateProvisionalElo(user1);
        }

        if (player2Games < minimumGamesForElo) {
            updateProvisionalElo(user2);
        }

        if (player1Games == minimumGamesForElo) {
            userRepository.updateEloIsProvisionalByUsername(false, user1.getUsername());
        }
        if (player2Games == minimumGamesForElo) {
            userRepository.updateEloIsProvisionalByUsername(false, user2.getUsername());
        }

        double currentGameScorePlayer1 = switch (gameStatus) {
            case FIRST_PLAYER_WON -> 1;
            case DRAW -> 0.5;
            default -> 0;
        };

        if (player1Games >= minimumGamesForElo) {
            updateActualElo(user1, user2, currentGameScorePlayer1);
        }

        if (player2Games >= minimumGamesForElo) {
            updateActualElo(user2, user1, 1 - currentGameScorePlayer1);
        }

    }
}
