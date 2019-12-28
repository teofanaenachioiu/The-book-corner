package com.example.firstapp.book_corner.item

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.firstapp.MainActivity
import com.example.firstapp.R
import com.example.firstapp.book_corner.data.Book
import kotlinx.android.synthetic.main.book_edit_fragment.*
import com.example.firstapp.core.TAG;
import kotlinx.android.synthetic.main.book_edit_fragment.fab


class BookEditFragment : Fragment() {

    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: BookEditViewModel
    private var itemId: String? = null
    private var item: Book? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }


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
        toolbarSetup();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.book_edit_fragment, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            image_view_edit.setImageBitmap(imageBitmap)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")

        setupViewModel()

        fab.setOnClickListener {
            Log.v(TAG, "save or update item")
            val book = item
            if (book != null) {
                book.title = book_title.text.toString()
                book.author = book_author.text.toString()
                book.gene = spinner.selectedItem.toString()
                book.user = "test"
//                book.user = AuthRepository.user!!.username

                if (context!!.isConnected) {
                    viewModel.saveOrUpdateItem(book)
                } else {
                    Toast.makeText(
                        this.context?.applicationContext,
                        "No internet",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        image_view_edit.setOnClickListener {
            dispatchTakePictureIntent();
        }

        // set data to
        val booksgene = resources.getStringArray(R.array.bookgene)

        // access the spinner
        if (spinner != null && activity != null) {
            val adapter = ArrayAdapter(
                activity!!.applicationContext,
                android.R.layout.simple_spinner_item, booksgene
            )
            spinner.adapter = adapter
        }

    }

    private fun toolbarSetup() {
        if (activity is MainActivity) {
            // add back button
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            item = Book("", "", "", "", "")
        } else {
            viewModel.getItemById(id).observe(this, Observer {
                Log.v(TAG, "update items")

                if (it != null) {
                    item = it
                    book_title.setText(it.title)
                    book_author.setText(it.author)

                    // spinner default selected item

                    val booksgene = resources.getStringArray(R.array.bookgene)
                    var index = booksgene.indexOf(it.gene)

                    if (index == -1) {
                        index = 0;
                    }
                    spinner.setSelection(index)
                }
            })
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            activity?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}
