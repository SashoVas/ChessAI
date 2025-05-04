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

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        //TODO:Make so the user choose who is first
        game.setCurrentTurnUser(user);
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
        if (game.getCurrentTurnUser()==null  || game.getCurrentTurnUser().getUsername().equals(game.getUser1().getUsername())){
            game.setCurrentTurnUser(game.getUser2());
        }
        else {
            game.setCurrentTurnUser(game.getUser1());
        }
        game.getMoves().add(currentMove);

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

        return new MoveResultDTO(bitBoard.getFen(),nextMove,bitBoard.getPossibleNextMoves());
    }
    @Transactional
    public MoveResultDTO makeAMoveToBot(MoveInputDTO move){
        Game game=getGame(move.roomId);
        BitBoard bitboard=makeAMove(game,move);
        return getBotMove(game,bitboard);
    }
    @Transactional
    public MoveResultDTO makeAMoveToPlayer(MoveInputDTO move){
        Game game=getGame(move.roomId);

        BitBoard bitBoard= makeAMove(game,move);
        return new MoveResultDTO(bitBoard.getFen(),move.move,bitBoard.getPossibleNextMoves());
    }
    public MoveResultDTO getCurrentGameState(InitialConnectDTO input){
        Game game=getGame(input.getRoomId());
        String currentFen=game.getCurrentFen();

        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        return new MoveResultDTO(currentFen,null,bitboard.getPossibleNextMoves());
    }
}
