package com.gnest.remember.data;

import android.os.Binder;


public class Memo extends Binder {
    private int id;
    private String memoText;
    private int position;
    private int textViewBackgroundId;
    private int textViewBackgroundSelectedId;

    public Memo(String memoText, int textViewBackgroundId, int textViewBackgroundSelectedId) {
        this.memoText = memoText;
        this.position = -1;
        this.textViewBackgroundId = textViewBackgroundId;
        this.textViewBackgroundSelectedId = textViewBackgroundSelectedId;
    }

    Memo(int id, String memoText, int position, int textViewBackgroundId, int textViewBackgroundSelectedId) {
        this.id = id;
        this.memoText = memoText;
        this.position = position;
        this.textViewBackgroundId = textViewBackgroundId;
        this.textViewBackgroundSelectedId = textViewBackgroundSelectedId;
    }

    public int getId() {
        return id;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTextViewBackgroundId() {
        return textViewBackgroundId;
    }

    public void setTextViewBackgroundId(int textViewBackgroundId) {
        this.textViewBackgroundId = textViewBackgroundId;
    }

    public int getTextViewBackgroundSelectedId() {
        return textViewBackgroundSelectedId;
    }

    public void setTextViewBackgroundSelectedId(int textViewBackgroundSelectedId) {
        this.textViewBackgroundSelectedId = textViewBackgroundSelectedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo = (Memo) o;

        if (id != memo.id) return false;
        if (position != memo.position) return false;
        return memoText != null ? memoText.equals(memo.memoText) : memo.memoText == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (memoText != null ? memoText.hashCode() : 0);
        result = 31 * result + position;
        return result;
    }
}
