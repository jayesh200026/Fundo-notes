package com.example.fundoapp.roomdb.dao

import androidx.room.*
import com.example.fundoapp.roomdb.entity.LabelEntity

@Dao
interface LabelDao {
    @Insert
    fun addLabel(label: LabelEntity): Long

    @Query("select * from Label ")
    fun getLabels(): List<LabelEntity>

    @Delete
    fun deleteLabel(labelEntity: LabelEntity): Int

    @Query("update Label set label=:newLabel where labelId=:labelID")
    fun updateLabel(labelID: String,newLabel:String): Int

    @Query("Delete from Label")
    fun clearTable()


}