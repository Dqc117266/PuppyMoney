package com.dqc.puppymoney.ui.view

import android.content.Context
import android.util.AttributeSet
import com.dqc.puppymoney.util.FontsOverride

class CustomFontEditText(context: Context?, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    var fontAssetName = "fonts/PingFang Regular.ttf"

    init {
        typeface = FontsOverride.getDefaultFont(context!!, fontAssetName)
    }

}