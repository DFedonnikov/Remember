package com.gnest.remember.presenter;

import com.gnest.remember.model.ArchiveMemoModelImpl;
import com.gnest.remember.model.db.data.Memo;

public class ArchiveFragmentPresenter extends ListFragmentPresenter {

    public ArchiveFragmentPresenter() {
        model = new ArchiveMemoModelImpl();
    }

    @Override
    public void processSingleChoiceClick(Memo memo, int verticalOrientationCode) {
        ifViewAttached(view -> {
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            }
        });
    }
}
