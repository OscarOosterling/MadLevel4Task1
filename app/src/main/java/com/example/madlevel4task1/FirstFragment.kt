package com.example.madlevel4task1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.InflateException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val products= arrayListOf<Product>()
    private val productAdapter = ProductsAdapter(products)

    private lateinit var productsRepository: ProductsRepository

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsRepository = ProductsRepository(requireContext())
        getProductsFromDatabase()

        initViews()
        fabAddProduct.setOnClickListener{

            ShowAddProductDialog()
        }
        fabDeleteAll.setOnClickListener{
            removeAllProducts()
        }


    }

    private fun removeAllProducts() {
        mainScope.launch { withContext(Dispatchers.IO){
            productsRepository.deleteAllProduct()
        }
            getProductsFromDatabase()
        }
    }

    @SuppressLint("InflateParams")
    private fun ShowAddProductDialog() {
        //Toast.makeText(activity,"test",Toast.LENGTH_LONG).show()
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.DialogTitle))

        val dialogLayout =layoutInflater.inflate(R.layout.add_product_dialog,null)
        val productName = dialogLayout.findViewById<EditText>(R.id.txt_product_name)
        val amount = dialogLayout.findViewById<EditText>(R.id.txt_amount)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.addtext){
            _:DialogInterface,_:Int->addProduct(productName,amount)
        }
        builder.show()
    }

    private fun addProduct(productName: EditText, amount: EditText) {
        if(validateFields(productName,amount)){
            mainScope.launch {
                val product = Product(ProductText = productName.text.toString(),ProductAmount = amount.text.toString().toInt())

            withContext(Dispatchers.IO){
                productsRepository.insertProduct(product)
            }
            getProductsFromDatabase()
            }
        }

    }

    private fun validateFields(productName: EditText, amount: EditText): Boolean {
        return if(productName.text.toString().isNotBlank()&&amount.text.toString().isNotBlank()){
            true
        }else{
            Toast.makeText(activity,"Fill in fields",Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun initViews() {
        rvShoppingList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvShoppingList.adapter = productAdapter
        rvShoppingList.addItemDecoration(
            DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)
        )
        createItemTouchHelper().attachToRecyclerView(rvShoppingList)
    }

    private fun getProductsFromDatabase(){
        mainScope.launch {
            val products = withContext(Dispatchers.IO){
                productsRepository.getAllProducts()
            }
            this@FirstFragment.products.clear()
            this@FirstFragment.products.addAll(products)
            this@FirstFragment.productAdapter.notifyDataSetChanged()
        }
    }

    private fun createItemTouchHelper(): ItemTouchHelper {

        // Callback which is used to create the ItemTouch helper. Only enables left swipe.
        // Use ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) to also enable right swipe.
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productsToDelete = products[position]

                MainScope().launch {
                    withContext(Dispatchers.IO){
                    productsRepository.deleteProduct(productsToDelete)
                }
                    getProductsFromDatabase()
                }

            }
        }
        return ItemTouchHelper(callback)
    }
}