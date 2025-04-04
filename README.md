# ♟️ Chess AI Web App
This is a full-stack Java Web Application designed to showcase and interact with a powerful, custom-built chess engine. The frontend is developed with Angular, while the backend is powered by Java, combining to deliver a responsive and feature-rich chess experience.

The core purpose of this application is to allow users to play against an AI-powered chess bot, driven by advanced search and evaluation algorithms. It offers a real-time, interactive environment where users can experience how a modern chess engine thinks and plays, making it an ideal tool for chess enthusiasts, developers, and AI learners.

In addition to the AI mode, the app also supports a multiplayer feature, enabling users to play head-to-head against other players through the web interface.

# Database diagram
![ChessDatabaseDiagram](https://github.com/user-attachments/assets/2f5d80e4-245f-4273-aee0-08e715ba5f8d)


# 🧠 Chess Engine Overview

This project is a high-performance chess engine built from scratch, focusing on efficiency and modern algorithmic techniques. It uses a **bitboard-based** board representation, enabling fast and compact move generation through **bitwise operations**.

## ♟️ Move Generation

The engine represents the board using **64-bit bitboards**, one for each piece type and color. Legal moves are generated through efficient bitwise manipulation, taking advantage of precomputed attack masks and sliding move bitboards to handle complex piece movement (like bishops, rooks, and queens) with minimal computational overhead.

## 🔍 Search Algorithms

To evaluate possible positions, the engine uses a combination of powerful search algorithms:
- [**Minimax with Alpha-Beta Pruning**](https://www.chessprogramming.org/Alpha-Beta) – Core search algorithm for evaluating best possible moves while pruning unpromising branches.
- [**Principal Variation Search (PVS)**](https://www.chessprogramming.org/Principal_Variation_Search) – Enhances Alpha-Beta by assuming the first move is the best, reducing search effort for other branches.
- [**Null Move Pruning**](https://www.chessprogramming.org/Null_Move_Pruning) – Skips unlikely branches by simulating a "pass" move.
- [**Late Move Reductions (LMR)**](https://www.chessprogramming.org/Late_Move_Reductions) – Reduces search depth for less promising moves after trying principal ones.
- [**Razoring**](https://www.chessprogramming.org/Razoring) – Prunes moves early when a position appears clearly losing, avoiding deeper search in hopeless lines.
- [**Quiescence Search**](https://www.chessprogramming.org/index.php?title=Quiescence_Search&mobileaction=toggle_view_desktop) – Extends search at volatile positions (e.g., during captures) to avoid horizon effect.
- [**Transposition Tables**](https://www.chessprogramming.org/Transposition_Table) – Caches previously evaluated positions using **Zobrist Hashing** to prevent redundant computation.

## 📊 Evaluation Function

The evaluation function scores positions based on a blend of material, positional, and structural heuristics:
- [**Material Evaluation**](https://www.chessprogramming.org/Material) – Classic piece value scoring (e.g., pawn = 100, knight = 320, etc.).
- [**Positional Scoring**](https://www.chessprogramming.org/Piece-Square_Tables) – Uses **interpolated piece-square tables** for each piece to encourage strong positioning.
- [**Pawn Structure Analysis**](https://www.chessprogramming.org/Pawn_Structure) – Includes penalties and bonuses for:
  - [**Doubled Pawns**](https://www.chessprogramming.org/Doubled_Pawn)
  - [**Isolated Pawns**](https://www.chessprogramming.org/Isolated_Pawn)
  - [**Passed Pawns**](https://www.chessprogramming.org/Passed_Pawn)
- [**King Safety**](https://www.chessprogramming.org/King_Safety) – Evaluates pawn shield, exposed files, and nearby enemy threats.
- [**Open & Semi-Open Files**](https://www.chessprogramming.org/Half-open_File) – Bonuses for rooks and queens controlling these lanes.
- [**Mobility Bonus**](https://www.chessprogramming.org/Mobility) – Encourages control of the board through mobility and threat assessment.

## ⚙️ Move Ordering

Efficient move ordering is critical for pruning in deep searches. This engine applies several heuristics:
- [**MVV-LVA (Most Valuable Victim - Least Valuable Attacker)**](https://www.chessprogramming.org/MVV-LVA) – Prioritizes impactful captures.
- [**Principal Variation (PV) Moves**](https://www.chessprogramming.org/PV-Move) – Re-evaluates the best move from previous search iterations first.
- [**History Heuristic**](https://www.chessprogramming.org/History_Heuristic) – Remembers moves that historically led to good positions.
- [**Killer Moves**](https://www.chessprogramming.org/Killer_Move) – Caches strong non-capture moves that caused beta cutoffs in sibling branches.

## 🚀 Performance and Scalability

The engine is built with performance in mind and structured to allow multithreading, time controls, and integration with UCI-compatible interfaces in the future.
