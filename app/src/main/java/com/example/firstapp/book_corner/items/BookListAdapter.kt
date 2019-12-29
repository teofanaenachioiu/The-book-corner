package com.example.firstapp.book_corner.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapp.R
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.item.BookDetailsFragment
import kotlinx.android.synthetic.main.book_list_row.view.*

class BookListAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    // items list is initially empty
    // we will add items when we get data from server or local database
    var items = emptyList<Book>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    // when an item is clicked we navigate to item details fragment
    // we also send the id of item through ITEM_ID from BookDetailsFragment
    private var onItemClick: View.OnClickListener;

    init {
        onItemClick = View.OnClickListener { view ->
            val item = view.tag as Book

            fragment.findNavController().navigate(R.id.item_details_fragment, Bundle().apply {
                putString(BookDetailsFragment.ITEM_ID, item._id)
            })
        }
    }

    // set the inflater for view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.book_list_row, parent, false)
        return ViewHolder(view)
    }

    // set text and event for each item from list using ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textViewTitle.text = item.title
        holder.textViewAuthor.text = item.author
        holder.itemView.tag = item
        holder.itemView.setOnClickListener(onItemClick)
    }

    // number of items in list
    override fun getItemCount() = items.size

    // row view
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.textTitle
        val textViewAuthor: TextView = view.textAuth
    }
}
