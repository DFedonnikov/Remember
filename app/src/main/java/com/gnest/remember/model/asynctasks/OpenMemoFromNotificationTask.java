package com.gnest.remember.model.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.MySelectableAdapter;


/**
 * Created by DFedonnikov on 08.08.2017.
 */

public class OpenMemoFromNotificationTask extends AsyncTask<Long, Void, Integer> {

    private static final String INTRPTD_EXC = "Interrupted_Exception";
    private final NotificationOpenerHelper mHelper;
    private MySelectableAdapter mAdapter;
    private int notificationId;

    public OpenMemoFromNotificationTask(NotificationOpenerHelper helper, MySelectableAdapter adapter) {
        this.mHelper = helper;
        this.mAdapter = adapter;
    }

    @Override
    protected Integer doInBackground(Long... longs) {
        while (mAdapter.getMemos().isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.d(INTRPTD_EXC, "Interrupted exception in OpenMemoFromNotificationTask doInBackgroundMethod()");
            }
        }
        long id = longs[0];
        notificationId = (int) id;
        int memoPosition = -1;
        for (ClickableMemo memo : mAdapter.getMemos()) {
            if (memo.getId() == id) {
                memoPosition = memo.getPosition();
            }
        }
        return memoPosition;
    }

    @Override
    protected void onPostExecute(Integer position) {
        if (position != -1) {
            mHelper.openMemoFromNotification(position, notificationId);
        }
    }


    public interface NotificationOpenerHelper {
        void openMemoFromNotification(int position, int notificationId);
    }
}
