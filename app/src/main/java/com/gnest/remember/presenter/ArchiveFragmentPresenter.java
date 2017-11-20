package com.gnest.remember.presenter;

import com.gnest.remember.model.ArchiveMemoModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;

public class ArchiveFragmentPresenter extends ListFragmentPresenter {

    public ArchiveFragmentPresenter() {
        mModel = new ArchiveMemoModelImpl();
    }

    @Override
    public void processSingleChoiceClick(Memo memo, int verticalOrientationCode) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            }
        }
    }
}
