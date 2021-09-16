package my.edu.tarc.rewardreferralapp.functions

import android.view.ViewGroup
import android.widget.*

class ResetForm {
    fun resetAllField(group: ViewGroup) {
        var i = 0
        val count = group.childCount
        while (i < count) {
            val view = group.getChildAt(i)
            if (view is EditText) {
                view.text.clear()
            }
            if (view is RadioGroup) {
                view.clearCheck()
            }
            if (view is Spinner) {
                view.setSelection(0)
            }
            if (view is CheckBox) {
                if(view.isChecked) {
                    view.toggle()
                }
            }
            if (view is ViewGroup && view.childCount > 0) resetAllField(view)
            ++i
        }
    }
}