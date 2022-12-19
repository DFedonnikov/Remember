package com.gnest.remember.database.model.old;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Memo extends RealmObject {
    @PrimaryKey
    private int mId;
    private String mMemoText;
    private int mPosition;
    private String mColor;
    private long mAlarmDate;
    private boolean mIsAlarmSet;
    private boolean mSelected = false;
    private boolean mExpanded = false;


    public Memo() {
    }

    public Memo(String memoText, String color, boolean alarmSet) {
        this.mMemoText = memoText;
        this.mPosition = -1;
        this.mColor = color;
        this.mIsAlarmSet = alarmSet;
    }

    public Memo(int id, String memoText, int position, String color, long alarmDate, boolean alarmSet) {
        this.mId = id;
        this.mMemoText = memoText;
        this.mPosition = position;
        this.mColor = color;
        this.mAlarmDate = alarmDate;
        this.mIsAlarmSet = alarmSet;
    }

    public Memo(int id, String memoText, int position, String color, long alarmDate, boolean alarmSet, boolean isSelected, boolean isExpanded) {
        this(id, memoText, position, color, alarmDate, alarmSet);
        this.mSelected = isSelected;
        this.mExpanded = isExpanded;
    }

    public Memo(Memo memo) {
        this(memo.getId(), memo.getMemoText(), memo.getPosition(), memo.getColor(), memo.getAlarmDate(), memo.isAlarmSet());
    }

    public int getId() {
        return mId;
    }

    public String getMemoText() {
        return mMemoText;
    }

    public void setMemoText(String memoText) {
        this.mMemoText = memoText;
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

    public long getAlarmDate() {
        return mAlarmDate;
    }

    public void setAlarmDate(long alarmDate) {
        this.mAlarmDate = alarmDate;
    }

    public boolean isAlarmSet() {
        return mIsAlarmSet;
    }

    public void setAlarm(boolean alarmSet) {
        mIsAlarmSet = alarmSet;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo = (Memo) o;

        if (mId != memo.mId) return false;
        if (mPosition != memo.mPosition) return false;
        return mMemoText != null ? mMemoText.equals(memo.mMemoText) : memo.mMemoText == null;

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mMemoText != null ? mMemoText.hashCode() : 0);
        result = 31 * result + mPosition;
        return result;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "mId=" + mId +
                ", mMemoText='" + mMemoText + '\'' +
                ", mPosition=" + mPosition +
                ", mColor='" + mColor + '\'' +
                ", mAlarmDate=" + mAlarmDate +
                ", mIsAlarmSet=" + mIsAlarmSet +
                '}';
    }
}
