package Chess.TranspositionTable;

public class TranspositionTableEntry {
    int score;

    int depth;
    int type;
    int bestMove;

    public TranspositionTableEntry(int score,int depth,int type,int bestMove){
        this.score=score;
        this.depth=depth;
        this.type=type;
        this.bestMove=bestMove;
    }

}
