package com.almasb.tictactoe.minimax;

/**
 * Abstract superclass for all AI players with different strategies.
 * To construct an AI player:
 * 1. Construct an instance (of its subclass) with the game Board
 * 2. Call setSeed() to set the computer's seed
 * 3. Call move() which returns the next move in an int[2] array of {row, col}.
 *
 * The implementation subclasses need to override abstract method move().
 * They shall not modify Cell[][], i.e., no side effect expected.
 * Assume that next move is available, i.e., not game-over yet.
 *
 * Adapted from https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 */
public abstract class AIPlayer {
    protected int ROWS = 3;  // number of rows
    protected int COLS = 3;  // number of columns

    protected Cell[][] cells; // the board's ROWS-by-COLS array of Cells
    protected Seed mySeed;    // computer's seed
    protected Seed oppSeed;   // opponent's seed

    /** Constructor with reference to game board */
    public AIPlayer(Board board) {
        cells = board.cells;
    }

    /** Set/change the seed used by computer and opponent */
    public void setSeed(Seed seed) {
        this.mySeed = seed;
        oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    /** Abstract method to get next move. Return int[2] of {row, col} */
    public abstract int[] move();  // to be implemented by subclasses
}