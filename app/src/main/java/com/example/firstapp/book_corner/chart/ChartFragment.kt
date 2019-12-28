package com.example.firstapp.book_corner.chart

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.firstapp.MainActivity

import com.example.firstapp.R
import com.example.firstapp.core.TAG
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.chart_fragment.*

class ChartFragment : Fragment() {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private lateinit var viewModel: ChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolbarSetup();
        setHasOptionsMenu(true);
        return inflater.inflate(com.example.firstapp.R.layout.chart_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupChart()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_chart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    private fun toolbarSetup() {
        if (activity is MainActivity) {
            // add back button
            (activity as MainActivity).getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
            (activity as MainActivity)
        }
    }

    private fun setupChart() {
        Log.v(TAG, "On setup chart")

        viewModel = ViewModelProviders.of(this).get(ChartViewModel::class.java)


        val booksgene = resources.getStringArray(R.array.bookgene)

        viewModel.items.observe(this, Observer { items ->
            Log.v(TAG, "i get items!")

            val entries = viewModel.getPieEntries(booksgene)

            drawChart(entries);
            val size = entries.size
            Log.v(TAG, "Size of list for chart $size")
        })


    }

    private fun drawChart(entries: List<PieEntry>?) {
        val set = PieDataSet(entries, "Genes")
        val colors = java.util.ArrayList<Int>();
        colors.add(Color.rgb(155, 191, 224))
        colors.add(Color.rgb(232, 160, 154))
        colors.add(Color.rgb(251, 226, 159))
        colors.add(Color.rgb(198, 214, 143))
        set.colors = colors

        val data = PieData(set)

        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        pieChartView.data = data

        pieChartView.description.textSize = 50f
        pieChartView.description.text = "Books gene tracker"
        pieChartView.description.textAlign = Paint.Align.RIGHT
        pieChartView.description.isEnabled = false

        pieChartView.setEntryLabelTextSize(14f)
        pieChartView.setEntryLabelColor(Color.BLACK)

        pieChartView.invalidate() // refresh
    }

}