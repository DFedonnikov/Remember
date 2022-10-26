package com.gnest.remember.extensions

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.NavigationUI.*
import androidx.preference.PreferenceManager
import com.gnest.remember.ui.MainActivity

fun Fragment.setSupportActionBar(toolbar: Toolbar) = (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)

fun Fragment.supportActionBar(): ActionBar? = (requireActivity() as? AppCompatActivity)?.supportActionBar

fun Fragment.setupActionBarWithNavController(drawerLayout: DrawerLayout) {
    val navController = NavHostFragment.findNavController(this)
    val activity = requireActivity() as? AppCompatActivity
//    activity?.let { setupActionBarWithNavController(it, navController, drawerLayout) }
}

fun Fragment.getRenderParams() = (requireActivity() as? MainActivity)?.renderParams

fun Fragment.sharedPreferences() = PreferenceManager.getDefaultSharedPreferences(requireActivity())