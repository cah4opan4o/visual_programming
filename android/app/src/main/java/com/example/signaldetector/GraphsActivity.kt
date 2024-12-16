package com.example.signaldetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.signaldetector.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GraphsActivity : AppCompatActivity() {
    private lateinit var chartRSRP: LineChart
    private lateinit var chartRSRQ: LineChart
    private lateinit var dataSetRSRP: LineDataSet
    private lateinit var dataSetRSRQ: LineDataSet
    private var lastX = 0f

    private val signalReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val rsrp = intent.getIntExtra("RSRP", 0)
            val rsrq = intent.getIntExtra("RSRQ", 0)
            updateGraphs(rsrp, rsrq)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        initializeGraphs()
    }

    private fun initializeGraphs() {
        chartRSRP = findViewById(R.id.chartRSRP)
        chartRSRQ = findViewById(R.id.chartRSRQ)

        dataSetRSRP = LineDataSet(ArrayList(), "RSRP Values")
        dataSetRSRQ = LineDataSet(ArrayList(), "RSRQ Values")

        chartRSRP.data = LineData(dataSetRSRP)
        chartRSRQ.data = LineData(dataSetRSRQ)

        // Настройка графиков
        listOf(chartRSRP, chartRSRQ).forEach { chart ->
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)
        }
    }

    private fun updateGraphs(rsrp: Int, rsrq: Int) {
        dataSetRSRP.addEntry(Entry(lastX, rsrp.toFloat()))
        dataSetRSRQ.addEntry(Entry(lastX, rsrq.toFloat()))

        chartRSRP.data.notifyDataChanged()
        chartRSRP.notifyDataSetChanged()
        chartRSRP.invalidate()

        chartRSRQ.data.notifyDataChanged()
        chartRSRQ.notifyDataSetChanged()
        chartRSRQ.invalidate()

        lastX++
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(signalReceiver, IntentFilter("SIGNAL_UPDATE"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(signalReceiver)
    }
}