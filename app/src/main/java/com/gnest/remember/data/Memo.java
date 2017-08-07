package com.gnest.remember.data;

import android.os.Binder;


public class Memo extends Binder {
    private int mId;
    private String memoText;
    private int mPosition;
    private String mColor;
    private String mDate;


    public Memo(String memoText, String color, String date) {
        this.memoText = memoText;
        this.mPosition = -1;
        this.mColor = color;
        this.mDate = date;
    }

    Memo(int id, String memoText, int position, String color, String date) {
        this.mId = id;
        this.memoText = memoText;
        this.mPosition = position;
        this.mColor = color;
        this.mDate = date;
    }

    public int getId() {
        return mId;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String mColor) {
        this.mColor = mColor;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo = (Memo) o;

        if (mId != memo.mId) return false;
        if (mPosition != memo.mPosition) return false;
        return memoText != null ? memoText.equals(memo.memoText) : memo.memoText == null;

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (memoText != null ? memoText.hashCode() : 0);
        result = 31 * result + mPosition;
        return result;
    }
}
