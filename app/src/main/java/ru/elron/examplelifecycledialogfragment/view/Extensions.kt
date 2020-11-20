package ru.elron.examplelifecycledialogfragment.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.showButtonBack() {
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun Fragment.showButtonBack() {
    activity?.let {
        (it as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
fun Fragment.hideButtonBack() {
    activity?.let {
        (it as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
        it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}

fun Context.dpToPx(dp: Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}
