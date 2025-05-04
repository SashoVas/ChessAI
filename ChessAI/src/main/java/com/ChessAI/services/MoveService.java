package com.ChessAI.services;

import com.ChessAI.Chess.BitBoard;
import com.ChessAI.dto.InitialConnectDTO;
import com.ChessAI.dto.MoveInputDTO;
import com.ChessAI.dto.MoveResultDTO;
import com.ChessAI.exceptions.InvalidActionException.InvalidMoveException;
import com.ChessAI.models.GameStatus;
import com.ChessAI.repos.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {
    String currentFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";//TODO:Take from db

    @Autowired
    private EloCalculatorService eloCalculatorService;

    @Autowired
    private GameRepository gameRepository;

    private BitBoard makeAMove(MoveInputDTO move){
        //TODO:getFromDb
        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        List<String> possibleMoves=bitboard.getPossibleNextMoves();
        if(!possibleMoves.contains(move.move)){
            throw new InvalidMoveException();
        }
        int decodedMove= bitboard.algebraToCode(move.move);
        bitboard.makeAMove(decodedMove);
        //TODO:SaveToDb
        currentFen=bitboard.getFen();
        return bitboard;
    }
    private MoveResultDTO getBotMove(MoveInputDTO move, BitBoard bitBoard){
        int hashMove=bitBoard.getBestMoveIterativeDeepening(10,1,1);
        String nextMove=BitBoard.toAlgebra(hashMove);
        bitBoard.makeAMove(hashMove);
        currentFen=bitBoard.getFen();
        //TODO:SaveToDb

        GameStatus gameStatus = bitBoard.getCurrentGameStatus();
        if (gameStatus != GameStatus.IN_PROGRESS) {
            gameRepository.updateGameStatusByGameId(gameStatus, move.roomId);//TODO:SaveToDb above might cover this
            eloCalculatorService.updateElo(move.roomId, gameStatus);
        }

        return new MoveResultDTO(currentFen,nextMove,bitBoard.getPossibleNextMoves());
    }

    public MoveResultDTO makeAMoveToBot(MoveInputDTO move){
        return getBotMove(move,makeAMove(move));
    }
    public MoveResultDTO makeAMoveToPlayer(MoveInputDTO move){
        BitBoard bitBoard= makeAMove(move);

        GameStatus gameStatus = bitBoard.getCurrentGameStatus();
        if (gameStatus != GameStatus.IN_PROGRESS) {
            gameRepository.updateGameStatusByGameId(gameStatus, move.roomId);//TODO:SaveToDb above might cover this
            eloCalculatorService.updateElo(move.roomId, gameStatus);
        }

        return new MoveResultDTO(bitBoard.getFen(),move.move,bitBoard.getPossibleNextMoves());
    }
    public MoveResultDTO getCurrentGameState(InitialConnectDTO input){
        //TODO:Get From Db
        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        return new MoveResultDTO(currentFen,null,bitboard.getPossibleNextMoves());
    }
}
