package com.avinash.minesweeper.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.avinash.minesweeper.R;

public class Cell extends Button {
    private boolean mIsCovered = true;
    private Drawable mDefaultBackground;

    public Cell(Context context) {
        super(context);
        mDefaultBackground = this.getBackground();
    }

    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDefaultBackground = this.getBackground();
    }

    public Cell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDefaultBackground = this.getBackground();
    }

    public boolean isCovered() {
        return this.mIsCovered;
    }

    public void setMine() {
        this.mIsCovered = false;
        this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.setBackgroundResource(android.R.drawable.btn_star_big_on);
    }

    public void setFlag() {
        this.setBackgroundResource(R.mipmap.flag);
        this.mIsCovered = false;
    }

    public void displayMineCount(int mineCount) {
        this.mIsCovered = false;
        this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (mineCount > 0) {
            this.setText(Integer.toString(mineCount));
        }
    }
}
