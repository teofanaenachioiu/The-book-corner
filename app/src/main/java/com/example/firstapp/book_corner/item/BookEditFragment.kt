package com.example.firstapp.book_corner.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.firstapp.MainActivity
import com.example.firstapp.R
import com.example.firstapp.auth.data.AuthRepository
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.core.TAG
import kotlinx.android.synthetic.main.book_edit_fragment.*
import kotlinx.io.IOException
import java.io.File


class BookEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: BookEditViewModel
    private lateinit var photoPath: String
    private var itemId: String? = null
    private var item: Book? = null

    // we use this property to check Internet connection
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")

        toolbarSetup();
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.book_edit_fragment, container, false)
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
                book.user = "default_user"

                if (AuthRepository.user != null) {
                    book.user = AuthRepository.user!!.username
                }

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
            takePicture();
        }

        setAvatar();

        setDataInSpinner()
    }


    // access the spinner
    private fun setDataInSpinner() {
        if (spinner != null && activity != null) {
            val adapter = ArrayAdapter(
                activity!!.applicationContext,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.bookgene)
            )
            spinner.adapter = adapter
        }
    }


    // set URI to ImageView
    private fun setAvatar() {
        val path = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath

        val pathToImg = "$path/$itemId.jpg"

        val file = File(pathToImg)
        if (file.exists()) {
            image_view_edit.setImageURI(Uri.parse(pathToImg))
        }
    }

    // enable back button
    private fun toolbarSetup() {
        if (activity is MainActivity) {
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

                    // set spinner default selected item
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


    ////////////////////////////////// CAMERA ////////////////////////////////////////

    private val REQUEST_TAKE_PHOTO = 1

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context!!.packageManager) != null) {
            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
            }

            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    context!!,
                    "com.example.firstapp.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }
        }
    }


    // create create file in internal storage for image
    // image name is its ID followed by .jpg extension
    private fun createImageFile(): File? {
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File(storageDir, "$itemId.jpg")

        photoPath = image.absolutePath
        Log.v(TAG, "Image path: $photoPath")

        return image
    }

    // set image URI to ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            image_view_edit.setImageURI(Uri.parse(photoPath))
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
}