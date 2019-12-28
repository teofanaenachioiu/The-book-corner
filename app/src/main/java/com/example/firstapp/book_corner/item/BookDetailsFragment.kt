package com.example.firstapp.book_corner.item

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.firstapp.MainActivity

import com.example.firstapp.R
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.core.TAG
import kotlinx.android.synthetic.main.book_details_fragment.*

class BookDetailsFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: BookDetailsViewModel
    private var itemId: String? = null
    private var item: Book? = null


    //////////////////////////////////// FAB //////////////////////////////////////
    var isOpen = false;
    ///////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////// IMAGE /////////////////////////////////////////
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private var currentAnimator: Animator? = null

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private var shortAnimationDuration: Int = 0
    ///////////////////////////////////////////////////////////////////////////////////

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
        return inflater.inflate(R.layout.book_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BookDetailsViewModel::class.java)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()

        /////////////////////////////// FAB //////////////////////////

        val fab_close = AnimationUtils.loadAnimation(context?.applicationContext, R.anim.fab_close);
        val fab_open = AnimationUtils.loadAnimation(context?.applicationContext, R.anim.fab_open);

        fabb.setOnClickListener {
            isOpen = if (isOpen) {

                fab_edit.startAnimation(fab_close);
                fab_delete.startAnimation(fab_close);
                fab_edit.isClickable = false;
                fab_delete.isClickable = false;
                false;
            } else {
                fab_edit.startAnimation(fab_open);
                fab_delete.startAnimation(fab_open);
                fab_edit.isClickable = true;
                fab_delete.isClickable = true;
                true;
            }

        }

        fab_edit.setOnClickListener {
            Log.v(TAG, "edit item")
            findNavController().navigate(R.id.item_edit_fragment, Bundle().apply {
                putString(BookEditFragment.ITEM_ID, itemId)
            })
        }
        fab_delete.setOnClickListener {
            Log.v(TAG, "delete item")

            deleteItem()
        }

        thumb_button_1.setOnClickListener {
            zoomImageFromThumb(thumb_button_1, tableLayoutDetails, fabs, R.drawable.book)
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BookDetailsViewModel::class.java)
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
                    book_gene.setText(it.gene)
                }
            })
        }
    }


    //////////////////////////////////////// IMAGE ANIMATION ///////////////////////////
    private fun zoomImageFromThumb(
        thumbView: View,
        tableLayout: TableLayout,
        fabs: RelativeLayout,
        imageResId: Int
    ) {
        if (activity is MainActivity) {
            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            currentAnimator?.cancel()

            // Load the high-resolution "zoomed-in" image.
            val expandedImageView: ImageView =
                (activity as MainActivity).findViewById(R.id.expanded_image)
            expandedImageView.setImageResource(imageResId)

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
            val startBoundsInt = Rect()
            val finalBoundsInt = Rect()
            val globalOffset = Point()

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBoundsInt)
            (activity as MainActivity).findViewById<View>(R.id.rootLayout)
                .getGlobalVisibleRect(finalBoundsInt, globalOffset)
            startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
            finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

            val startBounds = RectF(startBoundsInt)
            val finalBounds = RectF(finalBoundsInt)

            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            val startScale: Float
            if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
                // Extend start bounds horizontally
                startScale = startBounds.height() / finalBounds.height()
                val startWidth: Float = startScale * finalBounds.width()
                val deltaWidth: Float = (startWidth - startBounds.width()) / 2
                startBounds.left -= deltaWidth.toInt()
                startBounds.right += deltaWidth.toInt()
            } else {
                // Extend start bounds vertically
                startScale = startBounds.width() / finalBounds.width()
                val startHeight: Float = startScale * finalBounds.height()
                val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
                startBounds.top -= deltaHeight.toInt()
                startBounds.bottom += deltaHeight.toInt()
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
            thumbView.alpha = 0f
            tableLayout.alpha = 0f
            fabs.alpha = 0f;
            fabs.isEnabled = false;
            expandedImageView.visibility = View.VISIBLE

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expandedImageView.pivotX = 0f
            expandedImageView.pivotY = 0f

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            currentAnimator = AnimatorSet().apply {
                play(
                    ObjectAnimator.ofFloat(
                        expandedImageView,
                        View.X,
                        startBounds.left,
                        finalBounds.left
                    )
                ).apply {
                    with(
                        ObjectAnimator.ofFloat(
                            expandedImageView,
                            View.Y,
                            startBounds.top,
                            finalBounds.top
                        )
                    )
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        currentAnimator = null
                    }
                })
                start()
            }

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            expandedImageView.setOnClickListener {
                currentAnimator?.cancel()

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                currentAnimator = AnimatorSet().apply {
                    play(
                        ObjectAnimator.ofFloat(
                            expandedImageView,
                            View.X,
                            startBounds.left
                        )
                    ).apply {
                        with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                        with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                    }
                    duration = shortAnimationDuration.toLong()
                    interpolator = DecelerateInterpolator()
                    addListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {
                            thumbView.alpha = 1f
                            tableLayout.alpha = 1f
                            fabs.alpha = 1f;
                            fabs.isEnabled = true;
                            expandedImageView.visibility = View.GONE
                            currentAnimator = null
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            thumbView.alpha = 1f
                            tableLayout.alpha = 1f
                            fabs.alpha = 1f;
                            fabs.isEnabled = true;
                            expandedImageView.visibility = View.GONE
                            currentAnimator = null
                        }
                    })
                    start()
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////

    }

    private fun deleteItem(): Boolean {

        Log.v(TAG, "delete item item $id")

        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle("Remove item")

        // Display a message on alert dialog
        builder.setMessage("Do you want to remove this book?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { dialog, which ->
            // Do something when user press the positive button
            Toast.makeText(
                context,
                "Book has been removed.",
                Toast.LENGTH_SHORT
            ).show()

            // Change the app background color
            Log.v(TAG, "notify delete item")

            viewModel.deleteItem(item!!._id)
//            findNavController().popBackStack()
        }


        // Display a negative button on alert dialog
        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(
                context,
                "You are not agree.",
                Toast.LENGTH_SHORT
            ).show()
        }


        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel") { _, _ ->
            Toast.makeText(
                context,
                "You cancelled the dialog.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

        return true;
    }

}
