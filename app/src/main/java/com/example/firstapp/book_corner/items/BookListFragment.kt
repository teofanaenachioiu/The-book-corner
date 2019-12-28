package com.example.firstapp.book_corner.items

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.firstapp.R
import com.example.firstapp.core.Api
import kotlinx.android.synthetic.main.book_list_fragment.*
import com.example.firstapp.core.TAG;
class BookListFragment : Fragment() {
    private lateinit var itemListAdapter: BookListAdapter
    private lateinit var itemListModel: BookListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.book_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        (activity as AppCompatActivity).supportActionBar?.show()

        setupItemList()
        fab.setOnClickListener {
            Log.v(TAG, "add new item")
            findNavController().navigate(R.id.item_details_fragment)
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
                val message = "Loading exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        itemListModel.loadItems()
        Toast.makeText(this.context?.applicationContext, Api.emailHolder.email, Toast.LENGTH_LONG).show()
    }
}
