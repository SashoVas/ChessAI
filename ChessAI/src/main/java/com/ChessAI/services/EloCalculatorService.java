package com.ChessAI.services;

import com.ChessAI.config.EloCalculatorConfig;
import com.ChessAI.models.*;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

//TODO: the description above is outdated - changes in provisional formula

@Service
public class EloCalculatorService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EloCalculatorConfig eloConfig;


    private double getAverageOpponentElo(User currentUser, List<Game> games) {
        double averageOpponentElo = 0;

        for (Game game : games) {
            if (!game.getUser1().getUsername().equals(currentUser.getUsername())) {
                averageOpponentElo += game.getUser1Elo();
            }
            else {
                averageOpponentElo += game.getUser2Elo();
            }
        }

        return averageOpponentElo / games.size();
    }

    private double getDelta(String username, List<Game> games) {
        double winCount = 0;
        double tieCount = 0;
        for (Game game : games) {
            if (game.getUser1().getUsername().equals(username)) {
                if (game.getUser1Color() == PlayerColor.WHITE && game.getGameStatus() == GameStatus.WINNER_WHITE) {
                    winCount++;
                }
                else if (game.getUser1Color() == PlayerColor.BLACK && game.getGameStatus() == GameStatus.WINNER_BLACK) {
                    winCount++;
                }
            }
            else {
                if (game.getUser2Color() == PlayerColor.WHITE && game.getGameStatus() == GameStatus.WINNER_WHITE) {
                    winCount++;
                }
                else if (game.getUser2Color() == PlayerColor.BLACK && game.getGameStatus() == GameStatus.WINNER_BLACK) {
                    winCount++;
                }
            }
            if (game.getGameStatus() == GameStatus.DRAW) {
               tieCount++;
            }
        }

        //Elo delta bounded between [-1, 1]
        double delta = (2 * winCount + tieCount) / games.size() - 1;
        //Scale the delta
        delta *= eloConfig.getProvisionalScalingConstant();

        return delta;
    }


    private void updateProvisionalElo(User user) {
        //NOTE: This list is always non-empty, because the user has played at least the current game
        List<Game> games = gameRepository.findByUsernameAndGameType(user.getUsername(), GameType.MULTIPLAYER);

        //avg opponent elo
        double provisionalElo = getAverageOpponentElo(user, games);

        //current provisional elo is average score of opps + delta
        provisionalElo += getDelta(user.getUsername(), games);

        //Clip elo just in case
        provisionalElo = Math.max(eloConfig.getMinProvisionalElo(), Math.min(eloConfig.getMaxProvisionalElo(), provisionalElo));

        int roundedElo = (int) Math.round(provisionalElo);
        userRepository.updateEloByUsername(roundedElo, user.getUsername());
    }

    private int getEloKFactor(String username) {
        Integer gameCount = gameRepository.findGameCount(username, GameType.MULTIPLAYER);
        if (gameCount < eloConfig.getMinGameKFactor()) {
            return eloConfig.getBigKFactor();
        }
        Integer elo = userRepository.getEloByUsername(username);
        if (elo < 2400) { //TODO: export in config - update tests as well
            return eloConfig.getMidKFactor();
        }
        return eloConfig.getSmallKFactor();
    }

    private void updateActualElo(User user, User opponent,  double score) {
        int K_factor = getEloKFactor(user.getUsername());
        int userElo = user.getEloRating();
        int opponentElo = opponent.getEloRating();

        double expectedScore = 1.0 / (1 + Math.pow(10, (opponentElo - userElo) / 400.0));
        int updatedElo = (int) (userElo + K_factor * (score - expectedScore));

        userRepository.updateEloByUsername(updatedElo, user.getUsername());
    }

    private double getPlayer1Score(PlayerColor color, GameStatus gameStatus) {
        double currentGameScorePlayer1 = switch (gameStatus) {
            case WINNER_WHITE -> 1;
            case DRAW -> 0.5;
            default -> 0;
        };
        if (color == PlayerColor.BLACK) {
            currentGameScorePlayer1 = 1 - currentGameScorePlayer1;
        }
        return currentGameScorePlayer1;
    }


    @Transactional
    public void updateElo(Game game) {
        if (game.getGameType() == GameType.BOT) {
            return; //No elo calculation for bot games
        }
        User user1 = game.getUser1();
        User user2 = game.getUser2();

        //extract total multiplayer games played by each player
        int player1Games = gameRepository.findGameCount(user1.getUsername(), GameType.MULTIPLAYER);
        int player2Games = gameRepository.findGameCount(user2.getUsername(), GameType.MULTIPLAYER);

        //if players have played less than minimumGamesForElo, update provisional elo
        if (player1Games < eloConfig.getMinimumGamesForElo()) {
            updateProvisionalElo(user1);
        }
        if (player2Games < eloConfig.getMinimumGamesForElo()) {
            updateProvisionalElo(user2);
        }

        //if players just played their minimumGamesForElo game, update their elo status to non-provisional
        if (player1Games == eloConfig.getMinimumGamesForElo()) {
            userRepository.updateEloIsProvisionalByUsername(false, user1.getUsername());
        }
        if (player2Games == eloConfig.getMinimumGamesForElo()) {
            userRepository.updateEloIsProvisionalByUsername(false, user2.getUsername());
        }

        //if players have played more than minimumGamesForElo, update their actual elo
        double currentGameScorePlayer1 = getPlayer1Score(game.getUser1Color(), game.getGameStatus());
        if (player1Games >= eloConfig.getMinimumGamesForElo()) {
            updateActualElo(user1, user2, currentGameScorePlayer1);
        }
        if (player2Games >= eloConfig.getMinimumGamesForElo()) {
            updateActualElo(user2, user1, 1 - currentGameScorePlayer1);
        }

    }
}
