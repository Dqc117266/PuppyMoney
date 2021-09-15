package com.dqc.puppymoney.ui.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R

class OrsoPageAdapter : RecyclerView.Adapter<OrsoPageAdapter.ViewHoder>() {
    private val WISH_TO_ENTER_MAX_COUNT: Int = 10;
    private lateinit var mContext: Context;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.wish_to_enter_rv_item, parent, false)
        mContext = parent.context
        return ViewHoder(view)
    }

    override fun onBindViewHolder(holder: ViewHoder, position: Int) {
        holder.mEditText.hint = String.format(mContext.getString(R.string.wish_to_enter_edit_hint), getChineseNumbers(position))
        holder.mEditText.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                holder.mCountText.text = String.format(mContext.getString(R.string.wish_to_enter_text_count), holder.mEditText.text.toString().length)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun getItemCount(): Int {
        return WISH_TO_ENTER_MAX_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getChineseNumbers(position: Int) : Char {
        return charArrayOf('一', '二', '三', '四', '五', '六', '七', '八', '九', '十')[position]
    }

    class ViewHoder(view: View) : RecyclerView.ViewHolder(view) {
        var mEditText: EditText
        var mCountText: TextView
        init {
            mEditText = view.findViewById(R.id.wish_to_enter_edit)
            mCountText = view.findViewById(R.id.wish_to_enter_text_count)
        }
    }

}