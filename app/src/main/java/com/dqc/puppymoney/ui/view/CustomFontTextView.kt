package com.dqc.puppymoney.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.dqc.puppymoney.util.FontsOverride

class CustomFontTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    var fontAssetName = "fonts/PingFang Regular.ttf"

    init {
        typeface = FontsOverride.getDefaultFont(context, fontAssetName)
    }

}