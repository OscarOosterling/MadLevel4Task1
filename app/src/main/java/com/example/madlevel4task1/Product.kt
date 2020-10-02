package com.example.madlevel4task1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product_Table")
data class Product(
    @ColumnInfo(name = "name")
    var ProductText:String,
    @ColumnInfo(name = "quantity")
    var ProductAmount:Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Long?=null
)