package my.edu.tarc.rewardreferralapp.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import my.edu.tarc.rewardreferralapp.R

class LoadingDialog(myActivity: Activity) {
    private var activity: Activity = myActivity
    private lateinit var dialog: AlertDialog

    fun showAlertDialog(){
        var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        var inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.circle_loading_alert,null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissAlertDialog(){
        dialog.dismiss()
    }
}