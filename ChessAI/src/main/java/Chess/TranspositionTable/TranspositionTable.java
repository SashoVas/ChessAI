package Chess.TranspositionTable;

import Chess.AIBot;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    Map<Long, TranspositionTableEntry> tt=new HashMap<>(1000000);
    public void addToTable(long hash,int score,int depth,int type){
        TranspositionTableEntry entry=new TranspositionTableEntry(score,depth,type);
        tt.put(hash,entry);
    }
    public int retrieveFromTable(long hash,int depth,int alpha,int beta){
        TranspositionTableEntry entry=tt.get(hash);
        //Based on the type of node, return the most optimal value
        if (entry != null && entry.depth >= depth) {
            if (entry.type == AIBot.exactBoundType) return entry.score;
            if (entry.type == AIBot.lowerBoundType && entry.score<=alpha) return alpha ;
            else if (entry.type == AIBot.upperBoundType && entry.score>=beta) return beta ;
        }
        //throw new InternalError();
        return AIBot.invalidValue;
    }
    public boolean containsKey(long hash){
        return tt.containsKey(hash);
    }
    public void clear(){
        tt.clear();
    }

}
