package dev.testing.sampleandtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.testing.sampleandtest.databinding.MenuButtonItemBinding

class MainMenuAdapter(private val menuList:ArrayList<String>, private val onClickListener: OnClickListener) : RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {

    class ViewHolder(val binding: MenuButtonItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MenuButtonItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return menuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        with(holder){
            binding.menuString.text = menu.toString()
            binding.menuString.setOnClickListener {
                onClickListener.onClick(menu)
            }
        }
    }

    interface OnClickListener{
        fun onClick(menuStr: String)
    }
}