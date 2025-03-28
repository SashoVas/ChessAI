package Chess.TranspositionTable;

import Chess.AIBot;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    Map<Long, TranspositionTableEntry> tt=new HashMap<>();

    public void addToTable(long hash,int score,int depth,int type,int bestMove){
        //Save score of mate with true distance to mate
        if(score<-AIBot.MATE_SCORE)
            score-=AIBot.ply;
        if(score>AIBot.MATE_SCORE)
            score+=AIBot.ply;

        tt.put(hash,new TranspositionTableEntry(score,depth,type,bestMove));
    }
    public int retrieveFromTable(long hash,int depth,int alpha,int beta){

        AIBot.pastBestMove= 0;

        TranspositionTableEntry entry=tt.get(hash);
        //Based on the type of node, return the most optimal value
        if (entry != null && entry.depth >= depth) {
            int score=entry.score;
            //Adjust mating score to be at true distance to mate
            if(score<-AIBot.MATE_SCORE)
                score+=AIBot.ply;
            if(score>AIBot.MATE_SCORE)
                score-=AIBot.ply;

            if (entry.type == AIBot.EXACT_BOUND_TYPE) return score;
            if (entry.type == AIBot.LOWER_BOUND_TYPE && score<=alpha) return alpha ;
            else if (entry.type == AIBot.UPPER_BOUND_TYPE && score>=beta) return beta ;

            //Set the best move in the current position
            AIBot.pastBestMove= entry.bestMove;
        }
        else if(entry != null){
            //Set the best move in the current position
            AIBot.pastBestMove= entry.bestMove;
        }
        //throw new InternalError();
        return AIBot.INVALID_VALUE;
    }
    public boolean containsKey(long hash){
        return tt.containsKey(hash);
    }
    public void clear(){
        tt.clear();
    }

}
