package com.dqc.puppymoney.ui.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.dqc.puppymoney.R
import kotlinx.android.synthetic.main.activity_photo_view.*
import java.io.File

class PhotoViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        val filePath: String = intent.getStringExtra("file_path")!!

        Glide.with(this)
            .load(File(filePath))
            .dontAnimate()
            .into(photo_view)

        photo_view_cancel.setOnClickListener {
            onBackPressed()
        }

        photo_view.attacher.setOnClickListener {
            onBackPressed()
        }
    }
}