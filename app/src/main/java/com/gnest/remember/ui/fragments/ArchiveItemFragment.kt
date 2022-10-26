package com.gnest.remember.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController

import com.gnest.remember.R
import com.gnest.remember.model.db.data.Memo
import com.gnest.remember.presenter.ArchiveFragmentPresenter
import com.gnest.remember.presenter.IListFragmentPresenter
import androidx.recyclerview.widget.RecyclerView

class ArchiveItemFragment : ListItemFragment() {

    override fun createPresenter(): IListFragmentPresenter {
        return ArchiveFragmentPresenter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Has to be empty, so it won't inflate its parents menu layout here
    }

//    override fun handleOnBackPressed(): Boolean = when {
//        myGridLayoutManager?.orientation == RecyclerView.HORIZONTAL -> super.handleOnBackPressed()
//        else -> {
//            findNavController().navigateUp()
//            true
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSingleChoiceMemoClicked(memo: Memo) {
        myGridLayoutManager
                ?.takeIf { it.orientation == RecyclerView.VERTICAL }
                ?.let { it.openItem(memo.position) }
    }

    override fun getArchiveActionPluralForm(plural: Int): String = when (plural) {
        2 -> getString(R.string.note_unarchived_message_2)
        1 -> getString(R.string.note_unarchived_message_1)
        else -> getString(R.string.note_unarchived_message)
    }
}