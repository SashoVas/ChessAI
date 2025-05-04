package com.ChessAI.services;

import com.ChessAI.Chess.BitBoard;
import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.dto.InitialConnectDTO;
import com.ChessAI.dto.MoveInputDTO;
import com.ChessAI.dto.MoveResultDTO;
import com.ChessAI.exceptions.InvalidActionException.InvalidMoveException;
import com.ChessAI.exceptions.InvalidActionException.InvalidRoomException;
import com.ChessAI.models.*;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public Game createGame(CreateGameDTO createGameDTO, UserDetails userDetails) {
        Game game = new Game();

        User user=userRepository.findByUsername(userDetails.getUsername()).get();
        //Since user is authorized, we know that user exists
        game.setUser1(user);
        //TODO:Implement random colors
        PlayerColor u1Color = PlayerColor.WHITE;
        game.setUser1Color(u1Color);
        game.setUser2Color(PlayerColor.getOpponentColor(u1Color));
        game.setCurrentTurnColor(PlayerColor.WHITE);
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

    private Game getGame(String roomId){
        Optional<Game> currentGame=gameRepository.findById(Integer.parseInt(roomId));
        if (currentGame.isEmpty()){
            throw new InvalidRoomException();
        }
        return currentGame.get();
    }
    private void updateGameAfterMove(Game game, String moveNr, String currentFen, BitBoard bitboard){
        String fenAfterMove=bitboard.getFen();
        game.setCurrentFen(fenAfterMove);
        Move currentMove=new Move();
        currentMove.setMoveNr(moveNr);
        currentMove.setInitialFen(currentFen);
        currentMove.setFinalFen(fenAfterMove);
        game.setCurrentTurn(game.getCurrentTurn() + 1);
        game.setCurrentTurnColor(PlayerColor.getOpponentColor(game.getCurrentTurnColor()));
        game.getMoves().add(currentMove);
        game.setGameStatus(bitboard.getState());
        gameRepository.save(game);
    }
    private BitBoard makeAMove(Game game, MoveInputDTO move){
        String currentFen=game.getCurrentFen();

        //Making the move
        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        List<String> possibleMoves=bitboard.getPossibleNextMoves();
        if(!possibleMoves.contains(move.move)){
            throw new InvalidMoveException();
        }
        int decodedMove= bitboard.algebraToCode(move.move);
        bitboard.makeAMove(decodedMove);
        //Saving the game to db
        updateGameAfterMove(game,move.move,currentFen,bitboard);

        return bitboard;
    }
    private MoveResultDTO getBotMove(Game game, BitBoard bitBoard){
        String currentFen=bitBoard.getFen();
        int hashMove=bitBoard.getBestMoveIterativeDeepening(10,1,1);
        String nextMove=BitBoard.toAlgebra(hashMove);
        bitBoard.makeAMove(hashMove);

        updateGameAfterMove(game,nextMove,currentFen,bitBoard);

        return new MoveResultDTO(bitBoard.getFen(),nextMove,bitBoard.getPossibleNextMoves(),bitBoard.getState());
    }
    @Transactional
    public MoveResultDTO makeAMoveToBot(MoveInputDTO move){
        Game game=getGame(move.roomId);
        BitBoard bitboard=makeAMove(game,move);
        GameStatus state=bitboard.getState();
        if (state != GameStatus.NOT_STARTED && state != GameStatus.IN_PROGRESS && state != GameStatus.UNKNOWN){
            return new MoveResultDTO(bitboard.getFen(),move.move,Collections.emptyList(),state);
        }

        return getBotMove(game,bitboard);
    }
    @Transactional
    public MoveResultDTO makeAMoveToPlayer(MoveInputDTO move){
        Game game=getGame(move.roomId);

        BitBoard bitBoard= makeAMove(game,move);

        return new MoveResultDTO(bitBoard.getFen(),move.move,bitBoard.getPossibleNextMoves(),bitBoard.getState());
    }
    public MoveResultDTO getCurrentGameState(InitialConnectDTO input){
        Game game=getGame(input.getRoomId());
        String currentFen=game.getCurrentFen();

        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        return new MoveResultDTO(currentFen,null,bitboard.getPossibleNextMoves(),bitboard.getState());
    }
}
