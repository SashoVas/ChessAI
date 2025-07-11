package com.ChessAI;

import com.ChessAI.Chess.BitBoard;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
class BitBoardTest {
    static Stream<Arguments> provideTestCasesForPerf() {
        return Stream.of(
                Arguments.of(BitBoard.defaultFen,1,20),
                Arguments.of(BitBoard.defaultFen,2,400),
                Arguments.of(BitBoard.defaultFen,3,8902),
                Arguments.of(BitBoard.defaultFen,4,197281),
                Arguments.of(BitBoard.defaultFen,5,4865609),
                Arguments.of(BitBoard.defaultFen,6,119060324),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",1,48),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",2,2039),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",3,97862),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",4,4085603),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",5,193690690),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",1,14),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",2,191),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",3,2812),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",4,43238),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",5,674624),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",1,6),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",2,264),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",3,9467),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",4,422333),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",5,15833292),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",1,44),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",2,1486),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",3,62379),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",4,2103487),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",5,89941194),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",1,46),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",2,2079),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",3,89890),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",4,3894594),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",5,164075551)
                );
    }
    static Stream<Arguments> provideTestCasesForAlgebra() {
        return Stream.of(
                Arguments.of(BitBoard.defaultFen),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - "),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - "),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - "),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 b - - "),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1"),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R b KQ - 1 8"),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 b - - 0 10")
        );
    }
    static Stream<Arguments> provideTestCasesForFenGeneration() {
        return Stream.of(
                Arguments.of(BitBoard.defaultFen),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 0 1"),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 1"),
                Arguments.of("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1"),
                Arguments.of("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 b - - 0 1"),
                Arguments.of("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1"),
                Arguments.of("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R b KQ - 0 1"),
                Arguments.of("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 b - - 0 1")
        );
    }
    @ParameterizedTest
    @MethodSource("provideTestCasesForPerf")
    void TestPerft(String fen,int depth,int expected) {
        BitBoard board=BitBoard.createBoardFromFen(fen);
        assertEquals(expected,board.perft(depth));
    }
    @ParameterizedTest
    @MethodSource("provideTestCasesForPerf")
    void TestPerftWithUndo(String fen,int depth,int expected) {
        BitBoard board=BitBoard.createBoardFromFen(fen);
        assertEquals(expected,board.perftWithUndo(depth));
    }
    @ParameterizedTest
    @MethodSource("provideTestCasesForAlgebra")
    void TestAlgebraTransformations(String fen){
        BitBoard board=BitBoard.createBoardFromFen(fen);
        List<Integer> moves;
        if (board.isWhiteOnTurn()){
            moves=board.generateMovesW();
        }
        else moves=board.generateMovesB();
        for(int move:moves){
            String algebraMove=BitBoard.toAlgebra(move);
            long fromAlgebra=board.algebraToCode(algebraMove);
            if(fromAlgebra==-1){
                fromAlgebra=board.algebraToCode(algebraMove);
            }
            assertEquals(fromAlgebra,move);
        }

    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForFenGeneration")
    void TestFenGeneration(String fen){
        BitBoard board=BitBoard.createBoardFromFen(fen);
        String generatedFen=board.getFen();
        assertEquals(generatedFen,fen);
    }

}