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
 * TODO сделать пример с ARG_CHILD_FRAGMENT_ID и "androidx.navigation"
 */
open class LifecycleDialogFragment : DialogFragment() {
    companion object {
        val TAG = LifecycleDialogFragment::class.java.simpleName

        const val ARG_ID                    = "id"
        const val ARG_TAG                   = "tag"
        const val ARG_TITLE                 = "title"
        const val ARG_MESSAGE               = "message"
        const val ARG_CANCELABLE            = "cancelable"
        const val ARG_INDEX                 = "index"
        const val ARG_ITEMS                 = "items"
        const val ARG_ITEMS_CHECKED         = "items_checked"
        const val ARG_FRAGMENT_ID           = "fragment_id"
        const val ARG_FRAGMENT_TAG          = "fragment_tag"
        const val ARG_CHILD_FRAGMENT_ID     = "child_fragment_id"

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
    fun withTag(tag: String): LifecycleDialogFragment {
        getBundle().putString(ARG_TAG, tag)
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
     * @param id идентификатор родительского фрагмента, в котором хранится наш фрагмент
     * @param childId идентификатор дочернего фрагмента который реализует методы интерфейса [Builder].
     *  Пример:
     *
     *  `withChildFragmentId(R.id.nav_host_fragment, id)`
     */
    fun withChildFragmentId(id: Int, childId: Int): LifecycleDialogFragment {
        getBundle().putInt(ARG_FRAGMENT_ID, id)
        getBundle().putInt(ARG_CHILD_FRAGMENT_ID, childId)
        return this
    }

    /**
     * @param tag тег фрагмента который реализует методы интерфейса [Builder]
     */
    fun withFragmentTag(tag: String): LifecycleDialogFragment {
        getBundle().putString(ARG_FRAGMENT_TAG, tag)
        return this
    }

    inline fun withBundle(function: (Bundle) -> Unit): LifecycleDialogFragment {
        function(getBundle())
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dlg: Dialog? = null

        activity?.let {
            var builder: Builder? = null

            val fragmentId = getBundle().getInt(ARG_FRAGMENT_ID)
            val childFragmentId = getBundle().getInt(ARG_CHILD_FRAGMENT_ID)

            // это фрагмент внутри NavHostFragment
            if (fragmentId != 0 && childFragmentId != 0) {
                val navHostFragment = it.supportFragmentManager.findFragmentById(fragmentId)
                val fr = navHostFragment!!.childFragmentManager.findFragmentById(childFragmentId)
                if (fr is Builder)
                    builder = fr
            }

            // это фрагмент
            if (builder == null && fragmentId != 0) {
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

    /**
     * возвращает [Listener] который может быть Activity или Fragment
     */
    private fun getListener(): Listener? {
        val fragmentId = getBundle().getInt(ARG_FRAGMENT_ID)
        val childFragmentId = getBundle().getInt(ARG_CHILD_FRAGMENT_ID)
        val act = requireActivity()

        // это фрагмент внутри NavHostFragment
        if (fragmentId != 0 && childFragmentId != 0) {
            val navHostFragment = act.supportFragmentManager.findFragmentById(fragmentId)
            val subFragment = navHostFragment!!.childFragmentManager.findFragmentById(childFragmentId)
            if (subFragment is Listener)
                return subFragment
        }

        // это фрагмент
        if (fragmentId != 0) {
            val fragment = act.supportFragmentManager.findFragmentById(fragmentId)
            if (fragment is Listener)
                return fragment
        }

        // это фрагмент
        val fragmentTag = getBundle().getString(ARG_FRAGMENT_TAG)
        if (fragmentTag != null) {
            val fragment = act.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment is Listener)
                return fragment
        }

        // это активити
        if (act is Listener)
            return act

        return null
    }

    override fun onResume() {
        super.onResume()
        val listener = getListener()
        if (listener != null) {
            listener.onShowLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val listener = getListener()
        if (listener != null) {
            listener.onCancelLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val listener = getListener()
        if (listener != null) {
            listener.onDismissLifecycleDialogFragment(getBundle().getInt(ARG_ID), this)
        }
    }

    fun getBundle(): Bundle {
        var bundle: Bundle? = arguments
        if (bundle == null) {
            bundle = Bundle()
            arguments = bundle
        }
        return bundle
    }

    fun getBundleTag(): String = getBundle().getString(ARG_TAG, TAG)
    fun getBundleTitle(): String? = getBundle().getString(ARG_TITLE)
    fun getBundleMessage(): String? = getBundle().getString(ARG_MESSAGE)
    fun getBundleIndex(): Int = getBundle().getInt(ARG_INDEX)
    fun getBundleItems(): Array<String>? = getBundle().getStringArray(ARG_ITEMS)
    fun getBundleCheckedItems(): BooleanArray? = getBundle().getBooleanArray(ARG_ITEMS_CHECKED)

    fun show(activity: FragmentActivity) = show(activity.supportFragmentManager, getBundleTag())
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
