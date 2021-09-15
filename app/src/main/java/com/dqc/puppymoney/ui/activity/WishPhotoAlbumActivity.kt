package com.dqc.puppymoney.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions.makeSceneTransitionAnimation
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.dao.WishListDao
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.ui.adapter.WishPhotoPageAdapter
import com.dqc.puppymoney.ui.adapter.WishPhotoSelectListAdapter
import com.dqc.puppymoney.ui.recyclerview.CenterLayoutManager
import com.dqc.puppymoney.ui.view.WalkAroundImageView
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.FileUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.activity_wish_photo_album.*
import kotlinx.android.synthetic.main.fg_wish_photo_album_item.*
import kotlinx.android.synthetic.main.wish_photo_page_item.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class WishPhotoAlbumActivity : BaseActivity(),
    WishPhotoSelectListAdapter.IClickItemCallback,
    View.OnClickListener, WishPhotoPageAdapter.IPhotoPageTouchLienter {

    private val CAMERA_COOD = 0
    private val IMAGE_RESULT_COOD = 1

    private var mSelectPoisition: Int = 0
    private var mPreviousPosition: Int = -1

    private var mWishListDao: WishListDao? = null

    private var mWishPhotoPageAdapter: WishPhotoPageAdapter? = null
    private var mWishPhotoSelectListAdapter: WishPhotoSelectListAdapter? = null

    private var mCenterLayoutManager: CenterLayoutManager? = null

    private var mWishPhotoList = ArrayList<WishPhotoAlbumBean>()
    private var mSelectedList: BooleanArray? = null
    private var mIsEditMode: Boolean = false

    private var mPrvWalkView: WalkAroundImageView? = null
    private var mBottomAddImgDialog: Dialog? = null
    private var mBottomDeleteDialog: Dialog? = null

    private var mWishText: String? = null

    private var mDoubleClickTime: Long = 0
    private var mIsViewEmpty: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_photo_album)
        mWishText = intent.getStringExtra("wish_text")

        initTitleBar()
        initWishListDao()
        initSelectRv()
        initPhotoPage()
    }

    private fun initTitleBar() {
        add_wish_photo_btn.setOnClickListener(this)
        wish_photo_back_btn.setOnClickListener(this)
        menu_more.setOnClickListener(this)
        cancel_edit_mode_btn.setOnClickListener(this)
        delete_iv.setOnClickListener(this)

        wish_photo_title.setText(mWishText)
        show_edit_count_tv.setText(getString(R.string.wish_photo_album_edit_mode_select_text, 0))
    }

    private fun initWishListDao() {

        mWishListDao = AppDatabase.getInstance(this).getWishListDao()
        updateWishPhotoList()
    }


    private fun initPhotoPage() {
        mWishPhotoPageAdapter = WishPhotoPageAdapter()
        mWishPhotoPageAdapter?.setPhotoPageTouchLienter(this)
        wish_photo_page_vp.adapter = mWishPhotoPageAdapter
        mWishPhotoPageAdapter?.setWishPhotoList(mWishPhotoList)

//        val child: View = wish_photo_page_vp.getChildAt(0)
//        if (child is RecyclerView) {
//            child.setOverScrollMode(View.OVER_SCROLL_NEVER)
//        }

        wish_photo_page_vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mSelectPoisition = position
                mWishPhotoSelectListAdapter?.selectItem(position)
                if (mPreviousPosition == -1) {
                    controlWalkArondAnimator(mSelectPoisition)
                }

                mCenterLayoutManager?.smoothScrollToPosition(wish_photo_select_rv, RecyclerView.State(), position)
                if (mWishPhotoList.size > 0) {
                    Glide.with(this@WishPhotoAlbumActivity)
                            .load(File(mWishPhotoList.get(position).imgPath))
                            .bitmapTransform(BlurTransformation(this@WishPhotoAlbumActivity, 14, 6))
                            .into(wish_photo_select_bg)
                } else {
                    wish_photo_select_bg.setImageResource(0)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (mWishPhotoList.size > 0
                                && mPreviousPosition != mSelectPoisition) {
                            controlWalkArondAnimator(mSelectPoisition)
                        }
                    }
                }
            }
        })
    }

    fun initSelectRv() {
        mCenterLayoutManager = CenterLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)

        mWishPhotoSelectListAdapter = WishPhotoSelectListAdapter()
        mWishPhotoSelectListAdapter?.setIClickItemCallback(this)
        mWishPhotoSelectListAdapter?.setWishPhotoList(mWishPhotoList)

        wish_photo_select_rv.layoutManager = mCenterLayoutManager
        wish_photo_select_rv.adapter = mWishPhotoSelectListAdapter
        (wish_photo_select_rv.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    private fun openGallery() {
        var intent: Intent
        if (Build.VERSION.SDK_INT < 19) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
        } else {
            intent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        startActivityForResult(intent, IMAGE_RESULT_COOD)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_COOD)
    }

    private fun controlWalkArondAnimator(position: Int) {
        var layoutManager = (wish_photo_page_vp.getChildAt(0) as RecyclerView).layoutManager
        if (mPrvWalkView != null) {
            mPrvWalkView?.stopAnimator()
        }
        var curWalkView: WalkAroundImageView = layoutManager?.findViewByPosition(position)?.findViewById(R.id.walk_around_iv)!!
        curWalkView.startAnimator()

        mPrvWalkView = curWalkView
        mPreviousPosition = mSelectPoisition
    }

    private fun showBottomAddImgDialog() {
        mBottomAddImgDialog = Dialog(this, R.style.BottomDialog)
        val contentView = LayoutInflater.from(this)
                .inflate(R.layout.add_image_dialog, null)
        mBottomAddImgDialog?.setContentView(contentView)
        contentView.findViewById<Button>(R.id.select_camera_btn).setOnClickListener(this)
        contentView.findViewById<Button>(R.id.select_gallery_btn).setOnClickListener(this)
        contentView.findViewById<Button>(R.id.picture_cancle).setOnClickListener(this)

        mBottomAddImgDialog?.setCanceledOnTouchOutside(true)
        mBottomAddImgDialog?.getWindow()?.setGravity(Gravity.BOTTOM)
        mBottomAddImgDialog?.getWindow()?.setWindowAnimations(R.style.BottomDialog_Animation)

        val layoutParams = mBottomAddImgDialog?.getWindow()?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        mBottomAddImgDialog?.getWindow()?.attributes = layoutParams

        mBottomAddImgDialog?.show()
    }

    private fun showBottomDeleteImgDialog() {
        mBottomDeleteDialog = Dialog(this, R.style.BottomDialog)
        val contentView = LayoutInflater.from(this)
                .inflate(R.layout.delete_image_dialog, null)
        mBottomDeleteDialog?.setContentView(contentView)
        contentView.findViewById<Button>(R.id.delete_select_btn).setOnClickListener(this)
        contentView.findViewById<Button>(R.id.cancel_delete).setOnClickListener(this)
        contentView.findViewById<TextView>(R.id.delete_tips_tv)
                .setText(getString(
                        R.string.wish_photo_album_confirm_delete_img_text,
                        getSelectCount(mSelectedList!!)))

        mBottomDeleteDialog?.setCanceledOnTouchOutside(true)
        mBottomDeleteDialog?.getWindow()?.setGravity(Gravity.BOTTOM)
        mBottomDeleteDialog?.getWindow()?.setWindowAnimations(R.style.BottomDialog_Animation)

        val layoutParams = mBottomDeleteDialog?.getWindow()?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        mBottomDeleteDialog?.getWindow()?.attributes = layoutParams

        mBottomDeleteDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                CAMERA_COOD -> {
                    var extra = data?.extras
                    var bitmap = extra?.get("data") as Bitmap?
//                    val stream = ByteArrayOutputStream()
//                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                    val byteArray: ByteArray = stream.toByteArray()
                    var fileName = FileUtil.getPhotoFileName()
                    FileUtil.writeBitmapToSdcard(bitmap!!, fileName)

                    val currentTime = DisplayUtil.getCurrentTime()
                    var stringBuffer = StringBuffer()
                    var addYear = stringBuffer.append(currentTime.mCurrentYear).append("年").toString()
                    stringBuffer.setLength(0)
                    var addMonthDay = stringBuffer.append(currentTime.mCurrentMonth + 1).append("月").append(currentTime.mCurrentDaysOfMonth).toString()
                    stringBuffer.setLength(0)
                    var addHourMinute = stringBuffer.append(DisplayUtil.numberAddZero(currentTime.mCurrentHours))
                            .append(":")
                            .append(DisplayUtil.numberAddZero(currentTime.mCurrentMinute)).toString()

                    var mWishPhotoAlbumBean = WishPhotoAlbumBean(0, mWishText!!, fileName,
                            FileUtil.getFileMD5(fileName),
                            addYear,
                            addMonthDay,
                            addHourMinute)
                    mWishListDao?.insertWishPhotoalbum(mWishPhotoAlbumBean)
                    updateWishPhotoList()
                }

                IMAGE_RESULT_COOD -> {
                    var uri = data?.data
                    if (data != null && uri != null) {
                        var imgPath = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            FileUtil.handleImageBeforeKitKat(data, this)
                        } else {
                            FileUtil.handleImageOnKitKat(data, this)
                        }

                        insertImage(imgPath)
                    }
                }
            }
        }
    }


    private fun insertImage(imgPath: String?) {
        if (imgPath != null) {
//            var bitmap = BitmapFactory.decodeFile(imgPath)
            var newPath = FileUtil.mAppPath + "/${mWishText}/" + FileUtil.getFileName(imgPath)

            if (imgPath.equals(newPath)) {
                Toast.makeText(this, R.string.wish_photo_album_img_is_existed, Toast.LENGTH_LONG).show()
                return
            }

            FileUtil.copyFile(imgPath, newPath, mWishText)
//            Log.d("DQC", "insertImage bitmaplen " + bitmap.width + " is null " + (mWishListDao != null))
            if (mWishListDao != null) {
                val existedList = mWishListDao?.wishPhotoalbumIsExisted(mWishText!!, FileUtil.getFileMD5(newPath)!!)
                Log.d("CCCCC", " isExisted " + existedList!!.size + " MD5 " + FileUtil.getFileMD5(newPath))
                if (existedList.size == 0) {
                    val currentTime = DisplayUtil.getCurrentTime()
                    var stringBuffer = StringBuffer()

                    var addYear = stringBuffer.append(currentTime.mCurrentYear).append("年").toString()
                    stringBuffer.setLength(0)
                    var addMonthDay = stringBuffer.append(currentTime.mCurrentMonth + 1).append("月").append(currentTime.mCurrentDaysOfMonth).append("日").toString()
                    stringBuffer.setLength(0)
                    var addHourMinute = stringBuffer.append(DisplayUtil.numberAddZero(currentTime.mCurrentHours))
                            .append(":")
                            .append(DisplayUtil.numberAddZero(currentTime.mCurrentMinute)).toString()

                    var mWishPhotoAlbumBean = WishPhotoAlbumBean(0, mWishText!!, newPath, FileUtil.getFileMD5(newPath)!!,
                            addYear, addMonthDay, addHourMinute)
                    mWishListDao?.insertWishPhotoalbum(mWishPhotoAlbumBean)
                    updateWishPhotoList()
                } else {
                    Toast.makeText(this, R.string.wish_photo_album_img_is_existed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateWishPhotoList() {
        FileUtil.fileNotExistHandle(mWishListDao!!)

        val photoAlbumList = mWishListDao?.getPhotoAlbumList(mWishText!!)

        for (i in 0..photoAlbumList!!.size - 1) {
            Log.d("updateWish", " ${photoAlbumList.get(i).imgPath}")
        }
        mIsViewEmpty = !(photoAlbumList.size > 0)

        if (mIsViewEmpty) {
            emptyViewControl(true)
        } else {
            emptyViewControl(false)
        }

        mWishPhotoList.clear()
        mWishPhotoList.addAll(photoAlbumList)
        mWishPhotoPageAdapter?.setWishPhotoList(mWishPhotoList)
        mWishPhotoSelectListAdapter?.setWishPhotoList(mWishPhotoList)
        mWishPhotoSelectListAdapter?.setEditModeSelect(false, BooleanArray(photoAlbumList.size))
    }

    fun emptyViewControl(isShow: Boolean) {
        if (isShow) {
            DisplayUtil.setViewVisibility(empty_icon, View.VISIBLE)
            DisplayUtil.setViewVisibility(empty_text, View.VISIBLE)
        } else {
            DisplayUtil.setViewVisibility(empty_icon, View.GONE)
            DisplayUtil.setViewVisibility(empty_text, View.GONE)
        }
    }

    override fun onItemClick(position: Int, itemView: WishPhotoSelectListAdapter.SelectListViewHolder) {
        wish_photo_page_vp.setCurrentItem(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWishPhotoList.clear()

        if (mPrvWalkView != null) {
            mPrvWalkView!!.stopAnimator()
            mPrvWalkView = null
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.add_wish_photo_btn -> {
                showBottomAddImgDialog()
            }

            R.id.select_camera_btn -> {
                openCamera()
                mBottomAddImgDialog?.dismiss()
            }

            R.id.select_gallery_btn -> {
                openGallery()
                mBottomAddImgDialog?.dismiss()
            }

            R.id.picture_cancle -> {
                mBottomAddImgDialog?.dismiss()
            }

            R.id.wish_photo_back_btn -> {
                onBackPressed()
            }

            R.id.menu_more -> {
                if (!preventKeyDownFast()) {
                    showPopupMenu(menu_more)
                }
            }

            R.id.cancel_edit_mode_btn -> {
                mWishPhotoPageAdapter?.cancelEditMode()
            }

            R.id.delete_iv -> {
                showBottomDeleteImgDialog()
            }

            R.id.delete_select_btn -> {
                editModeDeleteImg()
                updateWishPhotoList()
                mWishPhotoPageAdapter?.cancelEditMode()
                mBottomDeleteDialog?.dismiss()
            }

            R.id.cancel_delete -> {
                mBottomDeleteDialog?.dismiss()
            }

        }
    }

    private fun editModeDeleteImg() {
        var index = 0;
        var selectCount = 0
        if (mSelectedList!!.size > 0) {
            for (i in 0..mSelectedList!!.size - 1) {
                if (mSelectedList!!.get(i)) {
                    val photoBean = mWishPhotoList.get(i)
                    index = i
                    selectCount ++
                    mWishListDao?.deleteWishPhotoalbum(photoBean)
                    FileUtil.deleteFile(photoBean.imgPath)
                }
            }
        }
        if (mSelectedList!!.size > 2 && index == 0 && selectCount == 1) {
            Glide.with(this)
                    .load(File(mWishPhotoList.get(index + 1).imgPath))
                    .bitmapTransform(BlurTransformation(this@WishPhotoAlbumActivity, 14, 6))
                    .into(wish_photo_select_bg)
        }
    }

    private fun editModeTitleVery(isEditMode: Boolean) {
        if (isEditMode) {
            DisplayUtil.setViewVisibility(edit_mode_layout, View.VISIBLE)
            DisplayUtil.setViewVisibility(title_bar_layout, View.GONE)
        } else {
            DisplayUtil.setViewVisibility(edit_mode_layout, View.GONE)
            DisplayUtil.setViewVisibility(title_bar_layout, View.VISIBLE)
        }
    }

    private fun getSelectCount(selectedList: BooleanArray): Int {
        var count = 0
        for (i in 0..selectedList.size - 1) {
            if (selectedList[i]) {
                count ++
            }
        }
        return count
    }

    @SuppressLint("ResourceType")
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.wish_photo_album_menu)
        if (mIsViewEmpty) {
            popupMenu.menu.removeItem(R.id.wish_edit)
        } else {
            val get = popupMenu.menu.get(0)
            if (get.title.equals("音乐")) {
                popupMenu.menu.add(R.id.wish_edit)
            }
        }
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.wish_edit -> {
                        mWishPhotoPageAdapter?.editMode()
                        DisplayUtil.setViewVisibility(delete_iv, View.GONE)
                        return true
                    }

                    R.id.wish_music -> {
                        return true
                    }
                }
                return false
            }

        })
        popupMenu.show()
    }

    private fun preventKeyDownFast(): Boolean {
        return if (SystemClock.elapsedRealtime() - mDoubleClickTime < 200) { //防止按键过快
            true
        } else {
            mDoubleClickTime = SystemClock.elapsedRealtime()
            false
        }
    }

    private fun addWishBtnAnimator(view: View, visibility: Int) {

        if (visibility == View.VISIBLE) {
            DisplayUtil.setViewVisibility(add_wish_photo_btn, View.VISIBLE)
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(200).start()
            ObjectAnimator.ofFloat(view, "scaleX", 1f).setDuration(200).start()
            ObjectAnimator.ofFloat(view, "scaleY", 1f).setDuration(200).start()

        } else if (visibility == View.GONE) {

            var alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            alpha.duration = 200
            alpha.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    DisplayUtil.setViewVisibility(add_wish_photo_btn, View.GONE)
                }
            })
            alpha.start()

            ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.3f).setDuration(200).start()
            ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.3f).setDuration(200).start()
        }
    }

    override fun onClickPhotoPage(position: Int, filePath: String, holder: WishPhotoPageAdapter.PageViewHolder) {
        val intent = Intent(this, PhotoViewActivity::class.java)
        intent.putExtra("file_path", filePath)
        startActivity(intent,
                makeSceneTransitionAnimation(this,
                        holder.mWalkAroundIv,
                        "share").toBundle())
    }

    override fun onEditModeChanged(isEditMode: Boolean) {
        mIsEditMode = isEditMode

        if (isEditMode) {
            addWishBtnAnimator(add_wish_photo_btn, View.GONE)
        } else {
//            editModeCancelAllMark()
            addWishBtnAnimator(add_wish_photo_btn, View.VISIBLE)
        }
        editModeTitleVery(isEditMode)
    }

    override fun onEditModeSelectItem(selectedList: BooleanArray, curPosition: Int) {
        mSelectedList = selectedList
        mWishPhotoSelectListAdapter
                ?.setEditModeSelect(true, selectedList)
        val selectCount = getSelectCount(selectedList)

//        editModeSelectMark(curPosition)

        if (selectCount > 0) {
            DisplayUtil.setViewVisibility(delete_iv, View.VISIBLE)
        } else {
            DisplayUtil.setViewVisibility(delete_iv, View.GONE)
        }

        show_edit_count_tv.setText(getString(R.string.wish_photo_album_edit_mode_select_text, selectCount))
    }

//    private fun editModeSelectMark(positon: Int) {
//
//        wish_photo_select_rv.post {
//            val positionView = mCenterLayoutManager?.findViewByPosition(positon)
//
//            if (positionView != null && mSelectedList!!.size > positon) {
//                var selectPreView: FrameLayout = positionView.findViewById(R.id.bottom_select_preview)!!
//                var visibility = if (mSelectedList!!.get(positon)) {View.VISIBLE} else {View.GONE}
//                DisplayUtil.setViewVisibility(selectPreView, visibility)
//                mWishPhotoSelectListAdapter?.notifyItemChanged(positon)
//                Log.d("ViewTreeObserver", " isSelect ${mSelectedList!!.get(positon)} editModeSelectMark ${positon}")
//            }
//        }
//    }

//    private fun editModeCancelAllMark() {
//        for (i in 0..mSelectedList!!.size - 1) {
//            mSelectedList!!.set(i, false)
//            editModeSelectMark(i)
//        }
//    }

//    private fun sho

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
            if (mIsEditMode) {
                mWishPhotoPageAdapter?.cancelEditMode()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }
}