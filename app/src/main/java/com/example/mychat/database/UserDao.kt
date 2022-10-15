package com.example.mychat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDao {

    @Insert
    suspend fun insert(user: User)


    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM PostTable")
    fun getdata(): LiveData<List<User>>

    @Query("Delete From PostTable")
    suspend fun deleteAll()

    @Query("Select * from PostTable where id=:id")
    fun getPostData(id:Int):LiveData<User>
}