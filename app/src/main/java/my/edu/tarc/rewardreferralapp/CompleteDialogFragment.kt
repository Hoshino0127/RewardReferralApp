package my.edu.tarc.kotlinswipemenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import my.edu.tarc.rewardreferralapp.R

class CompleteDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView:View = inflater.inflate(R.layout.fragment_download_complete_dialog,container,false)

        return rootView
    }

}