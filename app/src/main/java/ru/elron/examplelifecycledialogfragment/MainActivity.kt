package ru.elron.examplelifecycledialogfragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import ru.elron.examplelifecycledialogfragment.view.LifecycleDialogFragment
import ru.elron.examplelifecycledialogfragment.view.LifecycleDialogFragmentBuilder
import ru.elron.examplelifecycledialogfragment.view.LifecycleDialogFragmentListener

/**
 * Этот пример показывает как сделать чтобы диалоги отображались после поворота экрана
 *
 * Вопрос
 * Из фрагмента диалог вызывается так же как и из активити
 */
class MainActivity : AppCompatActivity(), LifecycleDialogFragmentBuilder,
    LifecycleDialogFragmentListener {
    val TAG = MainActivity::class.java.simpleName

    val DIALOG_SIMPLE1      = 100
    val DIALOG_SIMPLE2      = 200
    val DIALOG_LIST1        = 300
    val DIALOG_LIST2        = 400
    val DIALOG_PARAMETER    = 500
    val DIALOG_PROGRESS     = 600

    val ARG_COUNT           = "arg_count"

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        button1.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_SIMPLE1)
                .show(this)
        }
        button2.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_SIMPLE2)
                .withTitle("Заголовок")
                .withMessage("Очень важное сообщение")
                .withCancelable(false)
                .show(this)
        }
        button3.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_LIST1)
                .show(this)

        }
        button4.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_LIST2)
                .withItems(arrayOf("PC", "PS", "XBOX", "Other"))
                .withCheckedItems(booleanArrayOf(true, false, false, false))
                .show(this)
        }
        button5.setOnClickListener {
            LifecycleDialogFragment().apply {
                withId(DIALOG_PARAMETER)
                getBundle().putInt(ARG_COUNT, 5)
            }.show(this)
        }
        button6.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_PROGRESS)
                .show(this)
        }

    }

    override fun getLifecycleDialogInstance(id: Int, dialogFragment: LifecycleDialogFragment): Dialog {
        val builder = AlertDialog.Builder(this)
        var dialog: Dialog

        when(id) {
            DIALOG_SIMPLE1 -> {
                builder.setTitle(R.string.dialog_simple1_title)
                builder.setMessage(R.string.dialog_simple1_message)
                builder.setPositiveButton(R.string.dialog_ok, null)
            }
            DIALOG_SIMPLE2 -> {
                builder.setTitle(dialogFragment.getBundleTitle())
                builder.setMessage(dialogFragment.getBundleMessage())
                builder.setPositiveButton(R.string.dialog_ok, null)
                builder.setNegativeButton(R.string.dialog_cancel, null)
            }
            DIALOG_LIST1 -> {
                builder.setTitle(R.string.dialog_list1_title)
                builder.setItems(
                    R.array.dialog_list2,
                    DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this, "index = $which", Toast.LENGTH_SHORT).show()
                    })
            }
            DIALOG_LIST2 -> {
                builder.setTitle(dialogFragment.getBundleTitle())
                builder.setMultiChoiceItems(
                    dialogFragment.getBundleItems(),
                    dialogFragment.getBundleCheckedItems(),
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        Toast.makeText(this, "#$which = $isChecked", Toast.LENGTH_SHORT).show()
                    })
                builder.setPositiveButton(R.string.dialog_ok, null)
            }
            DIALOG_PARAMETER -> {
                builder.setTitle(R.string.dialog_parameter_title)
                builder.setMessage(getString(
                    R.string.dialog_parameter_message,
                    dialogFragment.getBundle().getInt(ARG_COUNT)))
                builder.setPositiveButton(R.string.dialog_ok, null)
            }
            DIALOG_PROGRESS -> {
                builder.setView(R.layout.dialog_progress)
            }
        }

        dialog = builder.create()
        return dialog
    }

    override fun onShowLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment) {
        Log.d(TAG, "onShowLifecycleDialogFragment() $id")
        when(id) {
            DIALOG_PROGRESS -> {
                viewModel.progressLiveData.observe(this, progressObserver)
                viewModel.startBackgroundTask()
            }
        }
    }

    val progressObserver = Observer<String> {
        it?.let {
            // закрываем диалог
            LifecycleDialogFragment.find(this)?.dismiss()

            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            viewModel.progressLiveData.postValue(null)
        }
    }

    override fun onCancelLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment) {
        Log.d(TAG, "onCancelLifecycleDialogFragment() $id")

    }

    override fun onDismissLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment) {
        Log.d(TAG, "onDismissLifecycleDialogFragment() $id")

    }

}
