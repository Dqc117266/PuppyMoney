package jp.wasabeef.glide.transformations

import android.content.Context
import android.graphics.*
import android.os.Build
import android.renderscript.RSRuntimeException
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import jp.wasabeef.glide.transformations.internal.FastBlur
import jp.wasabeef.glide.transformations.internal.RSBlur

/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BlurTransformation @JvmOverloads constructor(context: Context, pool: BitmapPool = Glide.get(context).bitmapPool, radius: Int = MAX_RADIUS, sampling: Int = DEFAULT_DOWN_SAMPLING) : Transformation<Bitmap> {
    private val mContext: Context
    private val mBitmapPool: BitmapPool
    private val mRadius: Int
    private val mSampling: Int
    private val mColor: Int = 0x7f333333

    constructor(context: Context, radius: Int) : this(context, Glide.get(context).bitmapPool, radius, DEFAULT_DOWN_SAMPLING) {}
    constructor(context: Context, radius: Int, sampling: Int) : this(context, Glide.get(context).bitmapPool, radius, sampling) {}

    override fun transform(resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()
        val width = source.width
        val height = source.height
        val scaledWidth = width / mSampling
        val scaledHeight = height / mSampling
        var bitmap = mBitmapPool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap!!)
        canvas.scale(1 / mSampling.toFloat(), 1 / mSampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        val filter = PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_ATOP)
        paint.colorFilter = filter
        canvas.drawBitmap(source, 0f, 0f, paint)
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                RSBlur.blur(mContext, bitmap, mRadius)
            } catch (e: RSRuntimeException) {
                FastBlur.blur(bitmap, mRadius, true)
            }
        } else {
            FastBlur.blur(bitmap, mRadius, true)
        }
        return BitmapResource.obtain(bitmap, mBitmapPool)
    }

    override fun getId(): String {
        return "BlurTransformation(radius=$mRadius, sampling=$mSampling)"
    }

    companion object {
        private const val MAX_RADIUS = 25
        private const val DEFAULT_DOWN_SAMPLING = 1
    }

    init {
        mContext = context.applicationContext
        mBitmapPool = pool
        mRadius = radius
        mSampling = sampling
    }
}