import Chess.Board;
import Chess.Move;

import java.security.InvalidParameterException;
import java.util.List;

public class Main {
    public static boolean runTest(int depth,int expected,String fen,int color){
        if(fen == null){
            fen=Board.defaultFen;
        }
        Board board=Board.fenToBoard(fen);
        long startTime = System.currentTimeMillis();
        int result=board.search(color,depth);
        long endTime = System.currentTimeMillis();

        System.out.println("Depth "+Integer.toString(depth)+": "+
                Integer.toString(result)+" "+
                "TestResult: "+Boolean.toString(result==expected)+" "+
                "RunTime: "+Long.toString(endTime-startTime)+" milliseconds");

        if(result!=expected){
            throw new InvalidParameterException("greshkaa");
        }
        return result==expected;
    }
    public static void runTests(){
        //Standard Board
        runTest(1,20,Board.defaultFen,1);
        runTest(2,400,Board.defaultFen,1);
        runTest(3,8902,Board.defaultFen,1);
        runTest(4,197281,Board.defaultFen,1);
        runTest(5,4865609,Board.defaultFen,1);
        //runTest(6,119060324,Board.defaultFen,1);
        //More checks
        runTest(1,14,"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",1);
        runTest(2,191,"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",1);
        runTest(3,2812,"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",1);
        runTest(4,43238,"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",1);
        runTest(5,674624,"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",1);
        //Test after promotions
        //More complex position
        runTest(1,6,"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",1);
        //runTest(2,264,"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",1);
        //runTest(3,9467,"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",1);
        //runTest(4,422333,"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",1);
        //runTest(5,15833292,"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",1);
        //Test after promotions
        //Fork
        //runTest(1,44,"rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",1);
        //runTest(2,1486,"rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",1);
        //runTest(3,62379,"rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",1);
        //runTest(4,2103487,"rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",1);
        //runTest(5,89941194,"rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",1);

    }
    public static void main(String[] args) {
        runTests();
        //runTestsStandard();
        //Board board=Board.fenToBoard(Board.defaultFen);
        ////Move move=new Move(6,4,4,4);
        ////board.makeAMoveSearch(move);
        ////move=new Move(1,3,3,3);
        ////board.makeAMoveSearch(move);
        //board.printBoard();
        ////List<Move>moves=board.getMoves(1);
        //System.out.println(board.search(1,6));
    }
}