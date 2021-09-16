package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.GetEvidencesItemLayoutBinding
import my.edu.tarc.rewardreferralapp.data.File

class GetEvidenceAdapter(var fileNameList: MutableList<File>,var clickListener: DownloadListener):
    RecyclerView.Adapter<GetEvidenceAdapter.myViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.get_evidences_item_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return fileNameList.size
    }

    class myViewHolder private constructor(val binding: GetEvidencesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: File, clickListener: DownloadListener) {
            binding.file = item
            binding.executePendingBindings()
            binding.downloadListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GetEvidencesItemLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val fileName: TextView = binding.tvFileName

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = fileNameList[position]
        holder.fileName.text = currentItem.FileName
        holder.bind(currentItem!!, clickListener)
    }

    class DownloadListener(val clickListener: (file: File) -> Unit) {
        fun onClick(item: File) =
            clickListener(item)
    }
}