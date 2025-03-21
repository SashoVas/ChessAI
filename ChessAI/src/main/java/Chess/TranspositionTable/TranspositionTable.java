package Chess.TranspositionTable;

import Chess.AIBot;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    Map<Long, TranspositionTableEntry> tt=new HashMap<>(1000000);
    public void addToTable(long hash,int score,int depth,int type){
        //Save score of mate with true distance to mate
        if(score<-AIBot.mateScore)
            score-=AIBot.ply;
        if(score>AIBot.mateScore)
            score+=AIBot.ply;

        tt.put(hash,new TranspositionTableEntry(score,depth,type));
    }
    public int retrieveFromTable(long hash,int depth,int alpha,int beta){
        TranspositionTableEntry entry=tt.get(hash);
        //Based on the type of node, return the most optimal value
        if (entry != null && entry.depth >= depth) {
            int score=entry.score;
            //Adjust mating score to be at true distance to mate
            if(score<-AIBot.mateScore)
                score+=AIBot.ply;
            if(score>AIBot.mateScore)
                score-=AIBot.ply;

            if (entry.type == AIBot.exactBoundType) return score;
            if (entry.type == AIBot.lowerBoundType && score<=alpha) return alpha ;
            else if (entry.type == AIBot.upperBoundType && score>=beta) return beta ;
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
