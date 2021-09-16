package my.edu.tarc.rewardreferralapp.dialog

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import my.edu.tarc.rewardreferralapp.R

class ImageDialog(myActivity: Activity,imageUri: Uri) {
    private var imgUri: Uri = imageUri
    private var activity: Activity = myActivity
    private lateinit var dialog: AlertDialog

    fun showAlertDialog(){
        println(imgUri)
        var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        var inflater: LayoutInflater = activity.layoutInflater
        var view: View = inflater.inflate(R.layout.expandable_image,null)
        builder.setView(view)
        builder.setCancelable(false)

        var imgView = view.findViewById<PhotoView>(R.id.photo_view)

        Glide
            .with(imgView.context)
            .load(imgUri)
            .into(imgView)

        imgView.setOnClickListener(){
            dismissAlertDialog()
        }

        dialog = builder.create()
        dialog.show()
    }

    fun dismissAlertDialog(){
        dialog.dismiss()
    }
}