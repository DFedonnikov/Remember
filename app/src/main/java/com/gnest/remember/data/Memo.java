package com.gnest.remember.data;

import android.os.Binder;


public class Memo extends Binder {
    private int id;
    private String memoText;

    public Memo(String memoText) {
        this.memoText = memoText;
    }

    public Memo(int id, String memoText) {
        this.id = id;
        this.memoText = memoText;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo = (Memo) o;

        if (id != memo.id) return false;
        return memoText != null ? memoText.equals(memo.memoText) : memo.memoText == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (memoText != null ? memoText.hashCode() : 0);
        return result;
    }
}
