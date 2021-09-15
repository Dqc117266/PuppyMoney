package com.dqc.puppymoney.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.dqc.puppymoney.util.FontsOverride

@SuppressLint("AppCompatCustomView")
class CustomFontRadioButton(context: Context?, attrs: AttributeSet?) : AppCompatRadioButton(context, attrs) {

    var fontAssetName = "fonts/PingFang Regular.ttf"

    init {
        typeface = FontsOverride.getDefaultFont(context!!, fontAssetName)
    }

}