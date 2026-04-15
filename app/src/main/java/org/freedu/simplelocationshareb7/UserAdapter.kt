package org.freedu.simplelocationshareb7


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.freedu.simplelocationshareb7.databinding.ItemUserBinding

class UserAdapter(private val onItemClick: (AppUsers) -> Unit
) : ListAdapter<AppUsers, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppUsers>() {
            override fun areItemsTheSame(
                oldItem: AppUsers,
                newItem: AppUsers
            ) = oldItem.userId == newItem.userId
            override fun areContentsTheSame(
                oldItem: AppUsers,
                newItem: AppUsers
            ) = oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.tvUsername.text = user.username
        holder.binding.tvEmail.text = user.email
        holder.binding.tvLat.text= "Latitude: ${user.latitude ?: "N/A"}"
        holder.binding.tvLng.text= "Longitude: ${user.longitude ?: "N/A"}"

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }

    }
    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

}