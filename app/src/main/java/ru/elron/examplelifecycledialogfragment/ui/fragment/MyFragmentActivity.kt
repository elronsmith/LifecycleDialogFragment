package ru.elron.examplelifecycledialogfragment.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.elron.examplelifecycledialogfragment.R
import ru.elron.examplelifecycledialogfragment.view.showButtonBack

class MyFragmentActivity : AppCompatActivity() {
    private val TAG = MyFragmentActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_fragment)
        showButtonBack()
        Log.d(TAG, "onCreate: ")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
