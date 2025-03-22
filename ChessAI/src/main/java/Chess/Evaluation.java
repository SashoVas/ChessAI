package Chess;

public class Evaluation {

    public static int positional_score[][][] =

    {
        //pawn
                    {{0, 0, 0, 0, 0, 0, 0, 0,
                    98, 134, 61, 95, 68, 126, 34, -11,
                    -6, 7, 26, 31, 65, 56, 25, -20,
                    -14, 13, 6, 21, 23, 12, 17, -23,
                    -27, -2, -5, 12, 17, 6, 10, -25,
                    -26, -4, -4, -10, 3, 3, 33, -12,
                    -35, -1, -20, -23, -15, 24, 38, -22,
                    0, 0, 0, 0, 0, 0, 0, 0},

                    // knight
                    {-167, -89, -34, -49, 61, -97, -15, -107,
                            -73, -41, 72, 36, 23, 62, 7, -17,
                            -47, 60, 37, 65, 84, 129, 73, 44,
                            -9, 17, 19, 53, 37, 69, 18, 22,
                            -13, 4, 16, 13, 28, 19, 21, -8,
                            -23, -9, 12, 10, 19, 17, 25, -16,
                            -29, -53, -12, -3, -1, 18, -14, -19,
                            -105, -21, -58, -33, -17, -28, -19, -23},

                    // bishop
                    {-29, 4, -82, -37, -25, -42, 7, -8,
                            -26, 16, -18, -13, 30, 59, 18, -47,
                            -16, 37, 43, 40, 35, 50, 37, -2,
                            -4, 5, 19, 50, 37, 37, 7, -2,
                            -6, 13, 13, 26, 34, 12, 10, 4,
                            0, 15, 15, 15, 14, 27, 18, 10,
                            4, 15, 16, 0, 7, 21, 33, 1,
                            -33, -3, -14, -21, -13, -12, -39, -21},

                    // rook
                    {32, 42, 32, 51, 63, 9, 31, 43,
                            27, 32, 58, 62, 80, 67, 26, 44,
                            -5, 19, 26, 36, 17, 45, 61, 16,
                            -24, -11, 7, 26, 24, 35, -8, -20,
                            -36, -26, -12, -1, 9, -7, 6, -23,
                            -45, -25, -16, -17, 3, 0, -5, -33,
                            -44, -16, -20, -9, -1, 11, -6, -71,
                            -19, -13, 1, 17, 16, 7, -37, -26},

                    // queen
                    {-28, 0, 29, 12, 59, 44, 43, 45,
                            -24, -39, -5, 1, -16, 57, 28, 54,
                            -13, -17, 7, 8, 29, 56, 47, 57,
                            -27, -27, -16, -16, -1, 17, -2, 1,
                            -9, -26, -9, -10, -2, -4, 3, -3,
                            -14, 2, -11, -2, -5, 2, 14, 5,
                            -35, -8, 11, 2, 8, 15, -3, 1,
                            -1, -18, -9, 10, -15, -25, -31, -50},

                    // king
                    {-65, 23, 16, -15, -56, -34, 2, 13,
                            29, -1, -20, -7, -8, -4, -38, -29,
                            -9, 24, 2, -16, -20, 6, 22, -22,
                            -17, -20, -12, -27, -30, -25, -14, -36,
                            -49, -1, -27, -39, -46, -44, -33, -51,
                            -14, -14, -22, -46, -44, -30, -15, -27,
                            1, 7, -8, -64, -43, -16, 9, 8,
                            -15, 36, 12, -54, 8, -28, 24, 14}},

            {    //pawn
                    {0, 0, 0, 0, 0, 0, 0, 0,
                            178, 173, 158, 134, 147, 132, 165, 187,
                            94, 100, 85, 67, 56, 53, 82, 84,
                            32, 24, 13, 5, -2, 4, 17, 17,
                            13, 9, -3, -7, -7, -8, 3, -1,
                            4, 7, -6, 1, 0, -5, -1, -8,
                            13, 8, 8, 10, 13, 0, 2, -7,
                            0, 0, 0, 0, 0, 0, 0, 0},

                    // knight
                    {-58, -38, -13, -28, -31, -27, -63, -99,
                            -25, -8, -25, -2, -9, -25, -24, -52,
                            -24, -20, 10, 9, -1, -9, -19, -41,
                            -17, 3, 22, 22, 22, 11, 8, -18,
                            -18, -6, 16, 25, 16, 17, 4, -18,
                            -23, -3, -1, 15, 10, -3, -20, -22,
                            -42, -20, -10, -5, -2, -20, -23, -44,
                            -29, -51, -23, -15, -22, -18, -50, -64},

                    // bishop
                    {-14, -21, -11, -8, -7, -9, -17, -24,
                            -8, -4, 7, -12, -3, -13, -4, -14,
                            2, -8, 0, -1, -2, 6, 0, 4,
                            -3, 9, 12, 9, 14, 10, 3, 2,
                            -6, 3, 13, 19, 7, 10, -3, -9,
                            -12, -3, 8, 10, 13, 3, -7, -15,
                            -14, -18, -7, -1, 4, -9, -15, -27,
                            -23, -9, -23, -5, -9, -16, -5, -17},

                    // rook
                    {13, 10, 18, 15, 12, 12, 8, 5,
                            11, 13, 13, 11, -3, 3, 8, 3,
                            7, 7, 7, 5, 4, -3, -5, -3,
                            4, 3, 13, 1, 2, 1, -1, 2,
                            3, 5, 8, 4, -5, -6, -8, -11,
                            -4, 0, -5, -1, -7, -12, -8, -16,
                            -6, -6, 0, 2, -9, -9, -11, -3,
                            -9, 2, 3, -1, -5, -13, 4, -20},

                    // queen
                    {-9, 22, 22, 27, 27, 19, 10, 20,
                            -17, 20, 32, 41, 58, 25, 30, 0,
                            -20, 6, 9, 49, 47, 35, 19, 9,
                            3, 22, 24, 45, 57, 40, 57, 36,
                            -18, 28, 19, 47, 31, 34, 39, 23,
                            -16, -27, 15, 6, 9, 17, 10, 5,
                            -22, -23, -30, -16, -16, -23, -36, -32,
                            -33, -28, -22, -43, -5, -32, -20, -41},

                    // king
                    {-74, -35, -18, -18, -11, 15, 4, -17,
                            -12, 17, 14, 17, 17, 38, 23, 11,
                            10, 17, 23, 15, 20, 45, 44, 13,
                            -8, 22, 24, 27, 26, 33, 26, 3,
                            -18, -4, 21, 24, 27, 23, 9, -11,
                            -19, -3, 11, 21, 23, 16, 7, -9,
                            -27, -11, 4, 13, 14, 4, -5, -17,
                            -53, -34, -21, -11, -28, -14, -24, -43}}
    };
    public static int opening_phase_score = 6192;
    public static int endgame_phase_score = 518;
    public static int[][] material_score =
            {
                    {82, 337, 365, 477, 1025, 12000, -82, -337, -365, -477, -1025, -12000},
                    {94, 281, 297, 512, 936, 12000, -94, -281, -297, -512, -936, -12000}
            };
    public static int get_game_phase_score(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp)
    {


        int whitePieceScore = 0;
        int blackPieceScore = 0;

        whitePieceScore+=BitBoardMovesGenerator.countBits(wn)* material_score[0][1];
        whitePieceScore+=BitBoardMovesGenerator.countBits(wq)* material_score[0][4];
        whitePieceScore+=BitBoardMovesGenerator.countBits(wb)* material_score[0][2];
        whitePieceScore+=BitBoardMovesGenerator.countBits(wr)* material_score[0][3];

        blackPieceScore+=BitBoardMovesGenerator.countBits(bn)* -material_score[0][7];
        blackPieceScore+=BitBoardMovesGenerator.countBits(bq)* -material_score[0][10];
        blackPieceScore+=BitBoardMovesGenerator.countBits(bb)* -material_score[0][8];
        blackPieceScore+=BitBoardMovesGenerator.countBits(br)* -material_score[0][9];
        // return game phase score
        return whitePieceScore + blackPieceScore;
    }

    public static int openingPhase=0;
    public static int endgamePhase=1;
    public static int middlemenPhase =2;
    public static int[] mapping={5,4,1,2,3,0,11,10,7,8,9,6};
    public static int evaluateBoard(long board,int pieceType,int gamePhase,int gamePhaseScore){
        //score += (
        //        material_score[opening][piece] * game_phase_score +
        //                material_score[endgame][piece] * (opening_phase_score - game_phase_score)
        //) / opening_phase_score;

        int result=0;
        long current=board& -board;


        while(current!=0){
            if (gamePhase == middlemenPhase)
                result += (
                        material_score[openingPhase][mapping[pieceType]] * gamePhaseScore +
                                material_score[endgamePhase][mapping[pieceType]] * (opening_phase_score - gamePhaseScore)
                ) / opening_phase_score;
            else result += material_score[gamePhase][mapping[pieceType]];

            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            if(pieceType<6) {
                if (gamePhase == middlemenPhase)
                    result += (
                            positional_score[openingPhase][mapping[pieceType]][index] * gamePhaseScore +
                                    positional_score[endgamePhase][mapping[pieceType]][index] * (opening_phase_score - gamePhaseScore)
                    ) / opening_phase_score;
                else result += positional_score[gamePhase][mapping[pieceType]][index];
            }
            else{
                if (gamePhase == middlemenPhase)
                    result -= (
                        positional_score[openingPhase][mapping[pieceType]-6][blackPos] * gamePhaseScore +
                                positional_score[endgamePhase][mapping[pieceType]-6][blackPos] * (opening_phase_score - gamePhaseScore)
                ) / opening_phase_score;
                else result -= positional_score[gamePhase][mapping[pieceType]-6][blackPos];
            }
            board&=~current;
            current=board& -board;
        }
        return result;
    }
    public static  int evaluate(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color)
    {
        int gamePhaseScore = get_game_phase_score(wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        int gamePhase = -1;

        // current game phase
        if (gamePhaseScore > opening_phase_score) gamePhase = openingPhase;
        else if (gamePhaseScore < endgame_phase_score) gamePhase = endgamePhase;
        else gamePhase = middlemenPhase;


        // init piece & square
        int result=0;
        result+=evaluateBoard(wq,AIBot.WQUEEN_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(wn,AIBot.WKNIGHT_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(wb,AIBot.WBISHOP_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(wr,AIBot.WROOK_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(wp,AIBot.WPAWN_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(bq,AIBot.BQUEEN_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(bn,AIBot.BKNIGHT_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(bb,AIBot.BBISHOP_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(br,AIBot.BROOK_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(bp,AIBot.BPAWN_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(wk,AIBot.WKING_INDEX,gamePhase,gamePhaseScore);
        result+=evaluateBoard(bk,AIBot.BKING_INDEX,gamePhase,gamePhaseScore);

        return color==1?result:-result;
    }
}
