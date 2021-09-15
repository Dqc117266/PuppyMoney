package com.dqc.puppymoney.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.dao.WishListDao
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest

object FileUtil {
    var mBasePath: String? = Environment.getExternalStorageDirectory().toString()
    var mAppPath: String = mBasePath + "/PuppyMoney"
//    var mPhotoFileName: String = mAppPath + "/" + System.currentTimeMillis() + ".jpg"

    fun getPhotoFileName(): String {
        return mAppPath + "/" + System.currentTimeMillis() + ".jpg"
    }

    fun handleImageOnKitKat(data: Intent, context: Context?): String? {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1] // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null, context)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null, context)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        return imagePath
    }

    fun handleImageBeforeKitKat(data: Intent, context: Context?): String? {
        val uri = data.data
        return getImagePath(uri, null, context)
    }

    private fun getImagePath(uri: Uri?, selection: String?, context: Context?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor: Cursor? = context?.contentResolver?.query(uri!!, null, selection, null, null)
        if (cursor != null) {
//            var i = 0
//            while (i < cursor.columnCount) {
//                var ss = cursor.getColumnName(i)
//                i++
//            }
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    fun copyFile(oldPath: String?, newPath: String?, wishText: String?) {
        if (oldPath.equals(newPath)) {
            return
        }

        try {
            var file = File(mAppPath + "/${wishText}")
            if (!file.exists()) {
                file.mkdirs()
            }
            Log.d("ERROR", "e " + file.toString())

            val input = FileInputStream(oldPath) //可替换为任何路径何和文件名
            val output = FileOutputStream(newPath) //可替换为任何路径何和文件名

            val buf = ByteArray(1024)
            var bytesRead = 0
            while (input.read(buf).also({ bytesRead = it }) > 0) {
                output.write(buf, 0, bytesRead)
            }
            input.close()
            output.close()
        } catch (e: IOException) {
            Log.d("ERROR", "e " + e.toString())
        }
    }


    fun writeBitmapToSdcard(bitmap: Bitmap, filePath: String) {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageByteArray = stream.toByteArray()
            val fs = FileOutputStream(filePath)
            fs.write(imageByteArray)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getFileName(filePath: String): String {
        val split = filePath.split("/")
        return split.get(split.size - 1)
    }

    fun getFileMD5(filePath: String?): String? {
        try {
            val file = File(filePath)
            val ins: InputStream = FileInputStream(file)
            val digest: MessageDigest = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(1024)
            var len: Int
            while (ins.read(buffer).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            val bigInt = BigInteger(1, digest.digest())
            return bigInt.toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun isFileEqualeFile(filePath: String, filePath2: String): Boolean {
        return getFileMD5(filePath).equals(getFileMD5(filePath2))
    }

    fun fileNotExistHandle(wishListDao: WishListDao) {
        val wishPhotoList = wishListDao.getPhotoAlbumListAll()
        for (i in 0..wishPhotoList.size - 1) {
            var file = File(wishPhotoList.get(i).imgPath)
            if (!file.exists()) {
                wishListDao.deleteWishPhotoalbum(wishPhotoList[i])
            }
        }
    }

    fun deleteDir(dirName: String): Boolean {
        var file = File(dirName)

        if (!file.exists() || (!file.isDirectory)) {
            return false
        }

        val listFiles = file.listFiles()

        for (i in 0..listFiles.size - 1) {
            deleteAnyone(listFiles[i].absolutePath)
        }

        if (file.delete()) {
            Log.d("FileUtil", " ${dirName} 删除成功 ")
        }
        return true
    }

    fun deleteAnyone(fileName: String): Boolean {
        var file = File(fileName)

        if (!file.exists()) {
            return false
        } else {
            if (file.isFile) {
                return deleteFile(fileName)
            } else {
                return deleteDir(fileName)
            }
        }

    }

    fun deleteFile(fileName: String): Boolean {
        var file = File(fileName)

        if (file.exists() && file.isFile) {
            if (file.delete()) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }


}