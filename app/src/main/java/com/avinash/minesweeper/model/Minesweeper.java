package com.avinash.minesweeper.model;

import java.util.Random;

/**
 * Created by avinash.mohan on 8/10/2015.
 */
public class Minesweeper {
    public static final int BOARD_SIZE = 8;
    private static final int MINES_COUNT = 10;

    private int[][] mCells;
    private boolean[][] mMines;

    public void newGame() {
        this.mCells = new int[BOARD_SIZE][BOARD_SIZE];
        this.mMines = new boolean[BOARD_SIZE][BOARD_SIZE];

        placesMines();
        updateSurroundingCells();
    }

    private void placesMines() {
        Random rand = new Random();
        for (int count = 0; count < MINES_COUNT; ) {
            int rowIdx = rand.nextInt(BOARD_SIZE);
            int colIdx = rand.nextInt(BOARD_SIZE);

            if (mMines[rowIdx][colIdx] == false) {
                mMines[rowIdx][colIdx] = true;
                count++;
            }
        }
    }

    private void updateSurroundingCells() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int prevRow = row - 1;
                int nextRow = row + 1;
                int prevCol = col - 1;
                int nextCol = col + 1;

                for (int rowIdx = prevRow; rowIdx <= nextRow; rowIdx++) {
                    for (int colIdx = prevCol; colIdx <= nextCol; colIdx++) {
                        if (isInsideBoard(rowIdx, colIdx) && mMines[rowIdx][colIdx]) {
                            mCells[row][col]++;
                        }
                    }
                }
            }
        }
    }

    public int getCellData(int row, int col) {
        return mCells[row][col];
    }

    public boolean isMine(int row, int col) {
        return mMines[row][col];
    }

    public boolean isInsideBoard(int rowIdx, int colIdx) {
        return rowIdx >= 0 && rowIdx < BOARD_SIZE && colIdx >= 0 && colIdx < BOARD_SIZE;
    }
}
