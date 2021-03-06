package com.example.firstapp.book_corner.items

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.firstapp.R
import com.example.firstapp.book_corner.item.BookEditFragment
import com.example.firstapp.core.Api
import com.example.firstapp.core.ConnectivityReceiver
import kotlinx.android.synthetic.main.book_list_fragment.*
import com.example.firstapp.core.TAG;

class BookListFragment : Fragment() {

    private lateinit var itemListAdapter: BookListAdapter
    private lateinit var itemListModel: BookListViewModel

    // we use this property to check Internet connection
    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.book_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.v(TAG, "onActivityCreated")

        (activity as AppCompatActivity).supportActionBar?.show()

        setupItemList()

        fab.setOnClickListener {
            Log.v(TAG, "navigate from items list to add item")
            findNavController().navigate(R.id.item_edit_fragment, Bundle().apply {
                putString(BookEditFragment.WINDOW_NAME, "New book")
            })
        }
    }

    private fun setupItemList() {
        itemListAdapter = BookListAdapter(this)
        item_list.adapter = itemListAdapter

        itemListModel = ViewModelProviders.of(this).get(BookListViewModel::class.java)

        itemListModel.items.observe(this, Observer { items ->
            Log.v(TAG, "update items")
            itemListAdapter.items = items
        })

        itemListModel.loading.observe(this, Observer { loading ->
            Log.v(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })

        itemListModel.loadingError.observe(this, Observer { exception ->
            if (exception != null) {
                Log.v(TAG, "update loading error")
                val parentActivity = this.context?.applicationContext
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, "Server error - loading items", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // check for internet connection
        // refresh list only if there is internet connection
        if (context!!.isConnected) {
            Log.v(TAG, "There is internet connection: Load data from server")
            itemListModel.refresh()
        } else {
            Toast.makeText(context, "Read from local database", Toast.LENGTH_SHORT).show()
            Log.v(TAG, "There is no internet connection: Load data from local database")
            itemListAdapter.items = itemListModel.items.value!!
        }
    }
}
