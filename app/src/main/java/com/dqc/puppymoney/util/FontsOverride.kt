package com.dqc.puppymoney.util

import android.content.Context
import android.graphics.Typeface

class FontsOverride {

    companion object {
        var regular:Typeface? = null
        fun getDefaultFont(context: Context, fontAssetName: String): Typeface? {
            if (regular == null) {
                regular = Typeface.createFromAsset(context.assets, fontAssetName)
            }
            return regular
        }
    }

}