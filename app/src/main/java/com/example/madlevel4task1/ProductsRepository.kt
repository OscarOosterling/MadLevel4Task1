package com.example.madlevel4task1

import android.content.Context

class ProductsRepository (context: Context){
    private var productDao:ProductDao
    init {
        val database = ProductsRoomDatabase.getDatabase(context)
        productDao=database!!.productDao()
    }

    suspend fun getAllProducts():List<Product>{
return productDao.getAllProducts()
    }
    suspend fun insertProduct(product: Product){
        productDao.insertProduct(product)
    }
    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }
    suspend fun deleteAllProduct(){
        productDao.deleteAllProducts()
    }

}