package com.dqc.puppymoney.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.dqc.puppymoney.R

@SuppressLint("AppCompatCustomView")
class RoundImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private var defaultWidth = 0
    private var defaultHeight = 0
    private var mRadius = 0f
    private var mImageViewWidth = 0

    init {
        if (attrs != null) {
            var a = context?.obtainStyledAttributes(attrs, R.styleable.round_image_view)
            mRadius = a!!.getDimensionPixelSize(R.styleable.round_image_view_fillet_radius, 0).toFloat()
            mImageViewWidth = a.getDimensionPixelSize(R.styleable.round_image_view_image_width, 0)
        }
    }

    fun setImageViewWidth(imageViewWidth: Int) {
        mImageViewWidth = imageViewWidth
        invalidate()
    }

    fun setFilletRadius(filletRadius: Int) {
        mRadius = filletRadius.toFloat()
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        drawRoundRadius(canvas)
        super.onDraw(canvas)
//        val bitmapDrawable = drawable as BitmapDrawable
//        val b = bitmapDrawable.bitmap
//        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)
//        val bitmap = DisplayUtil.getBitmapFromDrawable(drawable)
//        if (defaultWidth == 0)
//            defaultWidth = width
//        if (defaultHeight == 0)
//            defaultHeight = height
//
//        var roundBitmap = getCroppedRoundBitmap(bitmap!!, width);
//        canvas?.drawBitmap(roundBitmap, 0f, 0f, null)
    }

    fun drawRoundRadius(canvas: Canvas?) {
        if (width >= mRadius && height > mRadius) {
            var path = Path();
            //四个圆角
            path.moveTo(mRadius, 0f)
            path.lineTo(width - mRadius, 0f)
            path.quadTo(width.toFloat(), 0f, width.toFloat(), mRadius)
            path.lineTo(width.toFloat(), height - mRadius)
            path.quadTo(width.toFloat(), height.toFloat(), width - mRadius, height.toFloat())
            path.lineTo(mRadius, height.toFloat())
            path.quadTo(0f, height.toFloat(), 0f, height - mRadius)
            path.lineTo(0f, mRadius)
            path.quadTo(0f, 0f, mRadius, 0f)

            canvas?.clipPath(path)
        }
    }

    fun getCroppedRoundBitmap(bitmap: Bitmap, imageWidth: Int): Bitmap {
        var scaledSrcBmp: Bitmap
        var diameter = imageWidth

        var bmpWidth = bitmap.width
        var bmpHeight = bitmap.height
        var squareWidth:Int
        var squareHeight:Int
        var x:Int
        var y:Int
        var squareBitmap: Bitmap
        if (bmpHeight > bmpWidth) {
            squareHeight = bmpWidth
            squareWidth = squareHeight
            x = 0
            y = (bmpHeight - bmpWidth) / 2
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight)
        } else if (bmpHeight < bmpWidth) {
            squareHeight = bmpHeight
            squareWidth = squareHeight
            x = (bmpWidth - bmpHeight) / 2
            y = 0
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight)
        } else {
            squareBitmap = bitmap
        }

        if (squareBitmap.width != diameter
            || squareBitmap.height != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true)
        } else {
            scaledSrcBmp = squareBitmap
        }

        var output: Bitmap = Bitmap.createBitmap(scaledSrcBmp.width, scaledSrcBmp.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(output)

        var paint = Paint()
        var rectf = RectF(0f, 0f, scaledSrcBmp.getWidth().toFloat(),
            scaledSrcBmp.getHeight().toFloat())
        var rect = Rect(0, 0, scaledSrcBmp.getWidth(),
            scaledSrcBmp.getHeight())

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectf, mRadius, mRadius, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint)
        return output
    }
}