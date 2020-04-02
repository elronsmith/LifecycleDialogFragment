package ru.elron.examplelifecycledialogfragment.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import ru.elron.examplelifecycledialogfragment.R
import ru.elron.examplelifecycledialogfragment.databinding.FragmentMyBinding
import ru.elron.examplelifecycledialogfragment.view.LifecycleDialogFragment

class MyFragment : Fragment(), LifecycleDialogFragment.Builder {
    val DIALOG_SIMPLE1  = 100
    val DIALOG_SIMPLE2  = 200

    lateinit var binding: FragmentMyBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        binding.button1.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_SIMPLE1)
                .withFragmentId(R.id.fragment)
                .show(requireActivity())
        }
        binding.button2.setOnClickListener {
            LifecycleDialogFragment()
                .withId(DIALOG_SIMPLE2)
                .withFragmentTag("fragment_tag")
                .show(requireActivity())
        }

        return binding.root
    }

    override fun getLifecycleDialogInstance(
        id: Int,
        dialogFragment: LifecycleDialogFragment
    ): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        var dialog: Dialog

        when(id) {
            DIALOG_SIMPLE1 -> {
                builder.setTitle("title 1")
                builder.setMessage("Этот диалог создан во фрагменте и найден через id")
                builder.setPositiveButton("OK") { _, _ -> }
            }
            DIALOG_SIMPLE2 -> {
                builder.setTitle("title 2")
                builder.setMessage("Этот диалог создан во фрагменте и найден через tag")
                builder.setPositiveButton("OK") { _, _ -> }
            }
        }

        dialog = builder.create()
        return dialog
    }
}
