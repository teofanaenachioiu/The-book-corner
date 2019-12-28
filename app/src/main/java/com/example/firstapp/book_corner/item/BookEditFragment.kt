package com.example.firstapp.book_corner.item

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.firstapp.R
import com.example.firstapp.auth.data.AuthRepository
import com.example.firstapp.book_corner.data.Book
import kotlinx.android.synthetic.main.book_edit_fragment.*
import com.example.firstapp.core.TAG;
import kotlinx.android.synthetic.main.book_edit_fragment.fab
import kotlinx.android.synthetic.main.book_list_fragment.*

class BookEditFragment : Fragment() {

    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: BookEditViewModel
    private var itemId: String? = null
    private var item: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                itemId = it.getString(ITEM_ID).toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.book_edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save item")
            val book = item
            if (book != null) {
                book.title = book_title.text.toString()
                book.author = book_author.text.toString()
                book.gene = book_gene.text.toString()
                book.user = AuthRepository.user!!.username
                viewModel.saveOrUpdateItem(book)
            }
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BookEditViewModel::class.java)
        viewModel.fetching.observe(this, Observer { fetching ->
            Log.v(TAG, "update fetching")
            progresse.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(this, Observer { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(this, Observer { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = itemId

        if (id == null) {
            item = Book("","","","","")
        } else {
            viewModel.getItemById(id).observe(this, Observer {
                Log.v(TAG, "update items")

                if (it != null) {
                    item = it
                    book_title.setText(it.title)
                    book_author.setText(it.author)
                    book_gene.setText(it.author)
                }
            })
        }

    }

}
