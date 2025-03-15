package Chess.TranspositionTable;

public class TranspositionTableEntry {
    int score;

    int depth;
    int type;

    public TranspositionTableEntry(int score,int depth,int type){
        this.score=score;
        this.depth=depth;
        this.type=type;
    }

}
