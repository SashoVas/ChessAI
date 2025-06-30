package com.ChessAI.services;

import com.ChessAI.Chess.BitBoard;
import com.ChessAI.dto.*;
import com.ChessAI.exceptions.AuthenticationFailedException;
import com.ChessAI.exceptions.InvalidActionException.*;
import com.ChessAI.models.*;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.repos.MoveRepository;
import com.ChessAI.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EloCalculatorService eloCalculatorService;
    @Autowired
    private MoveRepository moveRepository;

    public GameResultDTO createGame(CreateGameDTO createGameDTO, UserDetails userDetails) {
        Game game = new Game();

        //Since user is authorized, we know that user exists
        User user=userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        game.setUser1(user);

        //TODO:Make so the user choose who is first

        PlayerColor u1Color = PlayerColor.getRandomColor();
        game.setUser1Color(u1Color);
        game.setUser2Color(PlayerColor.getOpponentColor(u1Color));
        game.setCurrentTurnColor(PlayerColor.WHITE);
        game.setUser1Elo(user.getEloRating());
        game.setIsUser1EloProvisional(user.IsEloProvisional());
        game.setGameType(createGameDTO.getGameType());
        game.setUser1TimeLeft(createGameDTO.getGameTimeSeconds());
        if (game.getGameType() == GameType.MULTIPLAYER) {
            game.setUser2TimeLeft(createGameDTO.getGameTimeSeconds());
            game.setGameStatus(GameStatus.NOT_STARTED);
        }
        else{
            game.setGameStatus(GameStatus.IN_PROGRESS);
        }
        game.setGameTimeSeconds(createGameDTO.getGameTimeSeconds());

        return GameResultDTO.fromEntity(gameRepository.save(game));
    }

    public Set<GameResultDTO> getFreeRooms() {
        return gameRepository
                .findByGameStatusAndGameType(GameStatus.NOT_STARTED, GameType.MULTIPLAYER)
                .stream()
                .map(GameResultDTO::fromEntity)
                .collect(Collectors.toSet());
    }

    public GameResultDTO joinRoom(String roomId, String username){
        Game game=getGame(roomId);
        if(game.getUser2() != null || game.getGameType() != GameType.MULTIPLAYER || username.equals(game.getUser1().getUsername())){
            throw new UnauthorizedGameAccessException();
        }

        //User is authorized, so he exists
        User user=userRepository.findByUsername(username).orElseThrow();
        game.setUser2(user);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        return GameResultDTO.fromEntity(game);
    }

    private Game getGame(String roomId){
        //TODO: Fix so that the rooms/games have string as id
        Optional<Game> currentGame=gameRepository.findByGameId(Integer.parseInt(roomId));
        if (currentGame.isEmpty()){
            throw new InvalidRoomException();
        }
        return currentGame.get();
    }

    private Game getGameWithMoves(String roomId){
        Optional<Game> currentGame=gameRepository.findByGameIdWithMoves(Integer.parseInt(roomId));
        if (currentGame.isEmpty()){
            throw new InvalidRoomException();
        }
        return currentGame.get();
    }

    public GameResultDTO getGameState(String roomId){
        return GameResultDTO.fromEntity(getGameWithMoves(roomId));
    }

    private void updateGameAfterMove(Game game, String moveNr, String currentFen, BitBoard bitboard){
        String fenAfterMove=bitboard.getFen();
        game.setCurrentFen(fenAfterMove);
        Move currentMove=new Move();
        currentMove.setMoveNr(moveNr);
        currentMove.setInitialFen(currentFen);
        currentMove.setFinalFen(fenAfterMove);
        currentMove.setGame(game);
        currentMove.setTurn(game.getCurrentTurn());
        game.setCurrentTurn(game.getCurrentTurn() + 1);
        game.setCurrentTurnColor(PlayerColor.getOpponentColor(game.getCurrentTurnColor()));
        //game.getMoves().add(currentMove);
        game.setGameStatus(bitboard.getState());

        gameRepository.save(game);
        moveRepository.save(currentMove);
        if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
            eloCalculatorService.updateElo(game);
        }
    }

    @Transactional
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

    @Transactional
    private MoveResultDTO makeBotMove(Game game,BitBoard bitBoard){
        String currentFen=bitBoard.getFen();
        int hashMove=bitBoard.getBestMoveIterativeDeepening(10,1,1);
        String nextMove=BitBoard.toAlgebra(hashMove);
        bitBoard.makeAMove(hashMove);

        updateGameAfterMove(game,nextMove,currentFen,bitBoard);

        return new MoveResultDTO(
                bitBoard.getFen(),
                nextMove,
                bitBoard.getPossibleNextMoves(),
                bitBoard.getState(),
                game.getCurrentTurnColor(),
                game.getUser1Color(),
                game.getGameType());
    }

    public MoveResultDTO getBotMove(String roomId,String username){
        Game game=getGame(roomId);
        if(!game.getUser1().getUsername().equals(username)){
            throw new UnauthorizedGameAccessException();
        }
        //It should be the bot's turn.
        if(game.getCurrentTurnColor()==game.getUser1Color()){
            throw new NotUserTurnException();
        }
        String currentFen=game.getCurrentFen();

        BitBoard bitBoard=BitBoard.createBoardFromFen(currentFen);

        return makeBotMove(game,bitBoard);
    }

    public MoveResultDTO makeAMoveToBot(MoveInputDTO move,String username){
        Game game=getGame(move.roomId);

        //Validation that the user can make the move
        if(game.getGameType()!=GameType.BOT){
            throw new UnauthorizedGameAccessException();
        }
        if(!game.getUser1().getUsername().equals(username) && !game.getUser2().getUsername().equals(username)){
            throw new UnauthorizedGameAccessException();
        }
        if(game.getUser1().getUsername().equals(username) && game.getCurrentTurnColor()!=game.getUser1Color()){
            throw new NotUserTurnException();
        }
        if(game.getGameStatus() != GameStatus.IN_PROGRESS && game.getGameStatus() != GameStatus.NOT_STARTED)
            throw new GameEndedException();

        // Make the move
        BitBoard bitboard=makeAMove(game,move);
        return new MoveResultDTO(
                bitboard.getFen(),
                move.move,
                Collections.emptyList(),
                bitboard.getState(),
                game.getCurrentTurnColor(),
                game.getUser1Color(),
                game.getGameType());
    }

    public MoveResultDTO makeAMoveToPlayer(MoveInputDTO move,String username){
        Game game=getGame(move.roomId);

        //Validation that the user can make the move
        if (game.getGameType()!=GameType.MULTIPLAYER){
            throw new UnauthorizedGameAccessException();
        }
        if(!game.getUser1().getUsername().equals(username) && !game.getUser2().getUsername().equals(username)){
            throw new UnauthorizedGameAccessException();
        }
        if(game.getUser1().getUsername().equals(username) && game.getCurrentTurnColor()!=game.getUser1Color()){
            throw new NotUserTurnException();
        }
        else if((game.getUser2()!=null && game.getUser2().getUsername().equals(username)) && game.getCurrentTurnColor()!=game.getUser2Color()){
            throw new NotUserTurnException();
        }
        if(game.getGameStatus() != GameStatus.IN_PROGRESS && game.getGameStatus() != GameStatus.NOT_STARTED)
            throw new GameEndedException();

        //Make the move
        BitBoard bitBoard= makeAMove(game,move);

        return new MoveResultDTO(
                bitBoard.getFen(),
                move.move,
                bitBoard.getPossibleNextMoves(),
                bitBoard.getState(),
                game.getCurrentTurnColor(),
                game.getUser1().getUsername().equals(username)?game.getUser1Color():game.getUser2Color(),
                game.getGameType());
    }

    public MoveResultDTO getCurrentGameState(InitialConnectDTO input,String username){
        //Everyone can get the game state. Used for the spectate mode
        Game game=getGame(input.getRoomId());
        String currentFen=game.getCurrentFen();
        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);

        if(game.getUser1().getUsername().equals(username) && game.getGameType()==GameType.BOT && game.getUser1Color()==PlayerColor.BLACK && game.getCurrentTurn()==0){
            return makeBotMove(game,bitboard);
        }

        return new MoveResultDTO(
                currentFen,
                null,
                bitboard.getPossibleNextMoves(),
                bitboard.getState(),
                game.getCurrentTurnColor(),
                game.getUser1().getUsername().equals(username)?game.getUser1Color():game.getUser2Color(),
                game.getGameType());
    }

    private GameStatus getGameStatusAfterLeave(Game game, String username){
        PlayerColor userThatLeftColor;
        if(game.getUser1() != null && game.getUser1().getUsername().equals(username)){
            userThatLeftColor=game.getUser1Color();
        }
        else if(game.getUser2() != null && game.getUser2().getUsername().equals(username)){
            userThatLeftColor=game.getUser2Color();
        }
        else {
            throw new AuthenticationFailedException();
        }
        if (userThatLeftColor == PlayerColor.WHITE){
            return GameStatus.WINNER_BLACK;
        }
        return GameStatus.WINNER_WHITE;
    }

    private void leaveGameByStatus(String username,GameStatus status){
        List<Game> games = gameRepository.findByUsernameAndGameStatus(username, status);
        // The user should be in only one game, so the for is not a problem
        // If the user could be in many games at a time, we could process all the game in batches,
        // so we maximize the performance
        for (Game game : games) {
            // Left from game against bot
            if (game.getGameType() == GameType.BOT) {
                if (game.getUser1Color() == PlayerColor.WHITE) {
                    game.setGameStatus(GameStatus.WINNER_BLACK);
                } else {
                    game.setGameStatus(GameStatus.WINNER_WHITE);
                }
                gameRepository.save(game);
                continue;
            }
            //The user leaves the game before any opponent joins
            if(game.getUser1() == null || game.getUser2() == null){
                gameRepository.delete(game);
                continue;
            }
            // Left from in-progress multiplayer game
            game.setGameStatus(getGameStatusAfterLeave(game, username));
            eloCalculatorService.updateElo(game);
            gameRepository.save(game);
        }
    }

    public void leaveGame(String username) {
        leaveGameByStatus(username,GameStatus.IN_PROGRESS);
        leaveGameByStatus(username,GameStatus.NOT_STARTED);
    }

    public MoveResultDTO surrender(String username,String roomId){
        Game game=getGame(roomId);
        if(game.getGameStatus() != GameStatus.IN_PROGRESS){
            throw new NotUserTurnException();
        }
        //User 1 surrenders
        if (game.getUser1()!=null && game.getUser1().getUsername().equals(username)){
            if (game.getUser1Color() == PlayerColor.WHITE){
                game.setGameStatus(GameStatus.WINNER_BLACK);
            }
            else{
                game.setGameStatus(GameStatus.WINNER_WHITE);
            }
        }
        //User 2 surrenders
        else if (game.getUser2()!=null && game.getUser2().getUsername().equals(username)){
            if (game.getUser2Color() == PlayerColor.WHITE){
                game.setGameStatus(GameStatus.WINNER_BLACK);
            }
            else{
                game.setGameStatus(GameStatus.WINNER_WHITE);
            }
        }
        //A spectator tries to surrender
        else {
            throw new UnauthorizedGameAccessException();
        }
        eloCalculatorService.updateElo(game);
        gameRepository.save(game);
        return new MoveResultDTO(
                game.getCurrentFen(),
                null,
                Collections.emptyList(),
                game.getGameStatus(),
                game.getCurrentTurnColor(),
                game.getUser1().getUsername().equals(username)?game.getUser1Color():game.getUser2Color(),
                game.getGameType());
    }

    public Set<GameResultDTO> getGamesByUser(String username) {
        List<Game> games = gameRepository.findAllByUsername(username);
        
        games.forEach(game -> {
            if (game.getGameStatus() == GameStatus.IN_PROGRESS) {
                game.setGameStatus(getGameStatusAfterLeave(game, username));
            }
        });
        
        return games.stream().map(GameResultDTO::fromEntity).collect(Collectors.toSet());
    }

    public void updateGameStatus(String username) {
        List<Game> games = gameRepository.findByUsernameAndGameStatus(username, GameStatus.IN_PROGRESS);
        for (Game game : games) {
            if (game.getUser1() != null && game.getUser1().getUsername().equals(username)) {
                // User1 disconnected
                if (game.getUser1Color() == PlayerColor.WHITE) {
                    game.setGameStatus(GameStatus.WINNER_BLACK);
                } else {
                    game.setGameStatus(GameStatus.WINNER_WHITE);
                }
            } else if (game.getUser2() != null && game.getUser2().getUsername().equals(username)) {
                // User2 disconnected
                if (game.getUser2Color() == PlayerColor.WHITE) {
                    game.setGameStatus(GameStatus.WINNER_BLACK);
                } else {
                    game.setGameStatus(GameStatus.WINNER_WHITE);
                }
            }
            gameRepository.save(game);
            eloCalculatorService.updateElo(game);
        }
    }

}
