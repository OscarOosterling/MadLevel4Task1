package com.example.madlevel4task1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Product::class], version = 1, exportSchema = false)

abstract class ProductsRoomDatabase : RoomDatabase(){
    abstract fun productDao():ProductDao

    companion object{
        private const val DATABASE_NAME = "PRODUCTS_DATABASE"

        @Volatile
        private var productsRoomDatabaseInstance:ProductsRoomDatabase?=null
        fun getDatabase(context: Context):ProductsRoomDatabase?{
            if(productsRoomDatabaseInstance==null) {
            synchronized(ProductsRoomDatabase::class.java){
                if(productsRoomDatabaseInstance==null){
                    productsRoomDatabaseInstance = Room.databaseBuilder(context.applicationContext,ProductsRoomDatabase::class.java,
                        DATABASE_NAME).build()
                }
            }
            }
            return productsRoomDatabaseInstance
        }
    }
}