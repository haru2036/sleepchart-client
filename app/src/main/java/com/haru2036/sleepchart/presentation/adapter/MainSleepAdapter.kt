package com.haru2036.sleepchart.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.haru2036.sleepchart.databinding.ItemSleepsBinding
import com.haru2036.sleepchart.domain.entity.Sleep

/**
 * Created by haru2036 on 2017/03/29.
 */

class MainSleepAdapter(context: Context, items:List<Sleep>) : ArrayAdapter<Sleep>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if(convertView != null && convertView.tag != null){
            val binding = convertView.tag as ItemSleepsBinding
             binding.sleep = getItem(position)
            return binding.root
        }else{
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val binding = ItemSleepsBinding.inflate(layoutInflater, parent, false)
            binding.sleep = getItem(position)
            binding.root.tag = binding
            return binding.root
        }
    }
    

}
