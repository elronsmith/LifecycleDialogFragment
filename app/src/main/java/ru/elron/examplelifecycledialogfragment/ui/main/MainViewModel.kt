package ru.elron.examplelifecycledialogfragment.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application): AndroidViewModel(application) {
    val TAG = MainViewModel::class.java.simpleName

    val progressLiveData = MutableLiveData<String>()
    var isWorking = false
    var list2Index = 0
    val list3TextArray = arrayOf("PC", "PS", "XBOX", "Other")
    val list3ValueArray = booleanArrayOf(true, false, false, false)

    fun startBackgroundTask() {
        if (isWorking) return
        isWorking = true

        Thread({
            doWork()
            progressLiveData.postValue("Успешно завершено!")
            isWorking = false
        }).start()
    }

    private fun doWork() {
        Log.d(TAG, "doWork()")
        Thread.sleep(5_000)
    }
}