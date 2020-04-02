package ru.elron.examplelifecycledialogfragment.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * Диалог который повторно отображается после поворота экрана.
 *
 * В: Как вызывать диалог из фрагмента ?
 * О: Точно также как из активити
 *
 * В: Зачем нужен тег ?
 * О: Если на экране отображается сразу 2 диалога, то нужно передавать уникальные теги
 *
 * В: Как закрыть диалог снаружи ?
 * О: Находим диалог через функцию find() и вызываем dismiss()
 *
 */
open class LifecycleDialogFragment : DialogFragment() {
    companion object {
        val TAG = LifecycleDialogFragment::class.java.simpleName

        val ARG_ID              = "id"
        val ARG_TAG             = "tag"
        val ARG_TITLE           = "title"
        val ARG_MESSAGE         = "message"
        val ARG_CANCELABLE      = "cancelable"
        val ARG_INDEX           = "index"
        val ARG_ITEMS           = "items"
        val ARG_ITEMS_CHECKED   = "items_checked"
        val ARG_FRAGMENT_ID     = "fragment_id"
        val ARG_FRAGMENT_TAG    = "fragment_tag"

        fun find(activity: FragmentActivity, tag: String = TAG): LifecycleDialogFragment? {
            return activity.supportFragmentManager.findFragmentByTag(tag) as LifecycleDialogFragment?
        }
    }

    /**
     * @param id идентификатор диалога, который будет создан
     */
    fun withId(id: Int): LifecycleDialogFragment {
        getBundle().putInt(ARG_ID, id)
        return this
    }

    fun withTitle(title: String): LifecycleDialogFragment {
        getBundle().putString(ARG_TITLE, title)
        return this
    }

    fun withMessage(message: String): LifecycleDialogFragment {
        getBundle().putString(ARG_MESSAGE, message)
        return this
    }

    /**
     * @param isCancelable false если нужно предотвратить закрытие диалога когда
     * пользователь тапает за пределы диалога. По-умолчанию true, закроет диалог если тапнуть
     * за пределы диалога.
     */
    fun withCancelable(isCancelable: Boolean): LifecycleDialogFragment {
        getBundle().putBoolean(ARG_CANCELABLE, isCancelable)
        return this
    }

    /**
     * @param index позиция элемента в списке
     */
    fun withIndex(index: Int): LifecycleDialogFragment {
        getBundle().putInt(ARG_INDEX, index)
        return this
    }

    /**
     * @param items свой список с элементами и одиночным выбором
     */
    fun withItems(items: Array<String>): LifecycleDialogFragment {
        getBundle().putStringArray(ARG_ITEMS, items)
        return this
    }

    /**
     * @param checkedItems свой список с элементами и множественным выбором
     */
    fun withCheckedItems(checkedItems: BooleanArray): LifecycleDialogFragment {
        getBundle().putBooleanArray(ARG_ITEMS_CHECKED, checkedItems)
        return this
    }

    /**
     * @param id идентификатор фрагмента который реализует методы интерфейса [Builder]
     */
    fun withFragmentId(id: Int): LifecycleDialogFragment {
        getBundle().putInt(ARG_FRAGMENT_ID, id)
        return this
    }

    /**
     * @param tag тег фрагмента который реализует методы интерфейса [Builder]
     */
    fun withFragmentTag(tag: String): LifecycleDialogFragment {
        getBundle().putString(ARG_FRAGMENT_TAG, tag)
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dlg: Dialog? = null

        activity?.let {
            var builder: Builder? = null

            val fragmentId = getBundle().getInt(ARG_FRAGMENT_ID)
            if (fragmentId != 0) {
                val fragment = it.supportFragmentManager.findFragmentById(fragmentId)
                if (fragment is Builder)
                    builder = fragment
            }

            if (builder == null) {
                val fragmentTag = getBundle().getString(ARG_FRAGMENT_TAG)
                if (fragmentTag != null) {
                    val fragment = it.supportFragmentManager.findFragmentByTag(fragmentTag)
                    if (fragment is Builder)
                        builder = fragment
                }
            }

            if (builder == null && it is Builder)
                builder = it

            if (builder != null)
                dlg = builder.getLifecycleDialogInstance(getBundle().getInt(ARG_ID), this)
        }

        val isCancelable = getBundle().getBoolean(ARG_CANCELABLE, true)
        if (!isCancelable)
            setCancelable(false)

        if (dlg == null)
            dlg = super.onCreateDialog(savedInstanceState)

        return dlg!!
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val act = activity

        if (act != null && act is Listener)
            act.onCancelLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val act = activity

        if (act != null && act is Listener)
            act.onDismissLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
    }

    override fun onResume() {
        super.onResume()
        val act = activity

        if (act != null && act is Listener)
            act.onShowLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
    }

    fun getTagValue(): String = getBundle().getString(ARG_TAG, TAG)

    fun getBundle(): Bundle {
        var bundle: Bundle? = arguments
        if (bundle == null) {
            bundle = Bundle()
            arguments = bundle
        }
        return bundle
    }

    fun getBundleTitle(): String? = getBundle().getString(ARG_TITLE)
    fun getBundleMessage(): String? = getBundle().getString(ARG_MESSAGE)
    fun getBundleIndex(): Int = getBundle().getInt(ARG_INDEX)
    fun getBundleItems(): Array<String>? = getBundle().getStringArray(ARG_ITEMS)
    fun getBundleCheckedItems(): BooleanArray? = getBundle().getBooleanArray(ARG_ITEMS_CHECKED)

    fun show(activity: FragmentActivity) = show(activity.supportFragmentManager, getTagValue())
    fun show(activity: FragmentActivity, tag: String) = show(activity.supportFragmentManager, tag)

    interface Builder {
        fun getLifecycleDialogInstance(id: Int, dialogFragment: LifecycleDialogFragment): Dialog
    }

    interface Listener {
        /**
         * Вызывается когда диалог показался на экране.
         * Необходимо в случае когда нужно сначала показать диалог и потом выполнить фоновую операцию.
         */
        fun onShowLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment)

        /**
         * Вызывается когда диалог был отменен пользователем
         */
        fun onCancelLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment)

        /**
         * Вызывается когда диалог закрылся
         */
        fun onDismissLifecycleDialogFragment(id: Int, dialogFragment: LifecycleDialogFragment)
    }
}
