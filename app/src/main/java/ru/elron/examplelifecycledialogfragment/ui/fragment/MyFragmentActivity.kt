package ru.elron.examplelifecycledialogfragment.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.elron.examplelifecycledialogfragment.R

class MyFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_fragment)
    }
}
