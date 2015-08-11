package com.avinash.minesweeper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.avinash.minesweeper.R;
import com.avinash.minesweeper.model.Minesweeper;

public class GameActivity extends Activity implements IGameControls {
    private TableLayout mUIGameBoard;
    private Cell[][] mCells;

    private boolean mIsWinner = false;
    private Minesweeper mGameBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mUIGameBoard = (TableLayout) findViewById(R.id.lBoard);
        final Button btnValidate = (Button) findViewById(R.id.btnValidate);

        btnValidate.setOnClickListener(mOnValidateClickListener);
        mUIGameBoard.setShrinkAllColumns(true);

        mCells = new Cell[Minesweeper.BOARD_SIZE][Minesweeper.BOARD_SIZE];
        mGameBoard = new Minesweeper();

        // Setup the Board.
        mGameBoard.newGame();
        // Load UI.
        loadBoard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_game:
                newGame();
                return true;
            case R.id.action_cheat:
                cheatGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void newGame() {
        // Generate new Game.
        mGameBoard.newGame();
        // Reset UI.
        mUIGameBoard.removeAllViews();
        loadBoard();
        mIsWinner = false;
    }

    @Override
    public void cheatGame() {
        displayMines();
    }

    private void loadBoard() {
        for (int rowIdx = 0; rowIdx < Minesweeper.BOARD_SIZE; rowIdx++) {
            TableRow row = new TableRow(GameActivity.this);

            for (int colIdx = 0; colIdx < Minesweeper.BOARD_SIZE; colIdx++) {
                Cell cell = new Cell(GameActivity.this);

                mCells[rowIdx][colIdx] = cell;
                // Save whether the cell is mine. If not, save row, col position in cell's tag.
                if (mGameBoard.isMine(rowIdx, colIdx)) {
                    mCells[rowIdx][colIdx].setTag(R.id.mine, true);
                } else {
                    mCells[rowIdx][colIdx].setTag(R.id.x, rowIdx);
                    mCells[rowIdx][colIdx].setTag(R.id.y, colIdx);
                }
                mCells[rowIdx][colIdx].setOnClickListener(mOnCellClickListener);
                mCells[rowIdx][colIdx]
                        .setOnLongClickListener(mOnCellLongClickListener);

                row.addView(mCells[rowIdx][colIdx]);
            }
            mUIGameBoard.addView(row);
        }
    }

    private void displayMines() {
        for (int rowIdx = 0; rowIdx < Minesweeper.BOARD_SIZE; rowIdx++) {
            for (int colIdx = 0; colIdx < Minesweeper.BOARD_SIZE; colIdx++) {
                if (mGameBoard.isMine(rowIdx, colIdx)) {
                    mCells[rowIdx][colIdx].setMine();
                }
            }
        }
    }

    private void displayResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int msgId = (mIsWinner ? R.string.game_winner : R.string.game_loser);
        builder.setMessage(msgId)
                .setPositiveButton(R.string.game_new,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                newGame();
                            }
                        })
                .setNegativeButton(R.string.game_exit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
        builder.create().show();
    }

    private void openSurroundingCells(int row, int col) {
        if (!mGameBoard.isMine(row, col)) {
            int surroundingMineCount = mGameBoard.getCellData(row, col);
            mCells[row][col].displayMineCount(surroundingMineCount);

            if (surroundingMineCount == 0) {
                int prevRow = row - 1;
                int nextRow = row + 1;
                int prevCol = col - 1;
                int nextCol = col + 1;

                for (int rowIdx = prevRow; rowIdx <= nextRow; rowIdx++) {
                    for (int colIdx = prevCol; colIdx <= nextCol; colIdx++) {
                        if (mGameBoard.isInsideBoard(rowIdx, colIdx)
                                && canUnCoverCell(rowIdx, colIdx)) {
                            openSurroundingCells(rowIdx, colIdx);
                        }
                    }
                }
            }
        }
    }

    private boolean canUnCoverCell(int row, int col) {
        return !mGameBoard.isMine(row, col) && mGameBoard.getCellData(row, col) >= 0
                && mCells[row][col].isCovered();
    }

    private boolean isGameFinished() {
        for (int rowIdx = 0; rowIdx < Minesweeper.BOARD_SIZE; rowIdx++) {
            for (int colIdx = 0; colIdx < Minesweeper.BOARD_SIZE; colIdx++) {
                if (mCells[rowIdx][colIdx].isCovered()) {
                    return false;
                }
            }
        }
        return true;
    }

    private View.OnClickListener mOnCellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag(R.id.mine) != null) {
                Object tag = v.getTag(R.id.mine);
                if ((Boolean) tag) {
                    v.setBackgroundColor(Color.RED);
                    v.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    // display all other mines
                    displayMines();
                    displayResultDialog();
                }
            } else {
                openSurroundingCells((Integer) v.getTag(R.id.x),
                        (Integer) v.getTag(R.id.y));
                mIsWinner = isGameFinished();
                if (mIsWinner) {
                    displayResultDialog();
                }
            }
        }
    };

    private View.OnLongClickListener mOnCellLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Cell cell = (Cell) v;
            if (v.getTag(R.id.mine) != null) {
                Object tag = v.getTag(R.id.mine);
                if ((Boolean) tag) {
                    cell.setFlag();
                }
            }
            return false;
        }
    };
    private View.OnClickListener mOnValidateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mIsWinner = isGameFinished();
            if (mIsWinner) {
                displayResultDialog();
            } else {
                Toast.makeText(GameActivity.this, R.string.complete_game,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
