package com.dqc.puppymoney.dao

import androidx.room.*
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.bean.WishToChooseBean

@Dao
interface WishListDao {
    @Query("SELECT * FROM wishToChooseBean order by wish_index asc")
    fun getWishList(): List<WishToChooseBean>

//    @Query("SELECT * FROM wishToChooseBean where (is_selected=:selected)")
//    fun getMostWantedWishList(selected: Boolean): List<WishToChooseBean>

    @Query("SELECT * FROM wishToChooseBean order by wish_index asc limit 0,3")
    fun getMostWantedWishList(): List<WishToChooseBean>

    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    fun insertWish(wish: WishToChooseBean)

    @Insert
    fun insertWishAll(wish: List<WishToChooseBean>)

    @Update
    fun updateWish(wish: WishToChooseBean)

    @Update
    fun updateWishList(wishList: List<WishToChooseBean>)

    @Delete
    fun deleteWish(wishToChooseBean: WishToChooseBean)

    @Delete
    fun deleteWishAll(wishToChooseBeans: List<WishToChooseBean>)

    @Query("SELECT * FROM wishphotoalbumbean")
    fun getPhotoAlbumListAll(): List<WishPhotoAlbumBean>

    @Query("SELECT * FROM wishphotoalbumbean where (wish_txt=:wishText)")
    fun getPhotoAlbumList(wishText: String): List<WishPhotoAlbumBean>

    @Query("SELECT * FROM wishphotoalbumbean where (wish_txt=:wishText) limit 0,5")
    fun getPhotoAlbumListLimit(wishText: String): List<WishPhotoAlbumBean>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertWishPhotoalbum(wishtoPhotoAlbumBean: WishPhotoAlbumBean)

    @Query("SELECT * FROM wishphotoalbumbean where (wish_txt=:wishText and md5_value=:md5)")
    fun wishPhotoalbumIsExisted(wishText:String, md5: String):List<WishPhotoAlbumBean>

    @Update
    fun updataWishPhotoalbum(wishtoPhotoAlbumBean: WishPhotoAlbumBean)

    @Delete
    fun deleteWishPhotoalbum(wishtoPhotoAlbumBean: WishPhotoAlbumBean)

//    @Query("")
//    fun successDiary
}