package com.desarollo.luisvillalobos.gardenkit.Activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.*
import com.desarollo.luisvillalobos.gardenkit.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.form_device.btn_home
import kotlinx.android.synthetic.main.graphs.*

class Graphs : AppCompatActivity(), View.OnClickListener {

    private lateinit var device: Device

    companion object {
        private val JSON_URL = "http://api.carriots.com/streams/?"
        private val API = "carriots.apikey"

        private val DEVICE = "device"
        private val AT_FROM = "&at_from="
        private val AT_TO = "&at_to="
        private val SORT = "&sort=at"
        private val ORDER = "&order=-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs)
        setup()

        //graphTest()
    }

    private fun setup() {
        //Screen
        SetUpActivity.hiderActionBar(this)
        SetUpActivity.hideStatusBar(this)
        SetUpActivity.hideSoftKeyboard(this)

        //Listeners
        btn_home.setOnClickListener(this)

        //GetExtras
        device = intent.getParcelableExtra(ListDevices.DEVICE_PARCEABLE_TAG)
    }

    //ClickListeners buttons implementation
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_home -> homeBtnClick()
        }
    }

    private fun homeBtnClick() {
        finish()
    }

    //ManageGraphs
    private fun lineDataSetConfig(lineDataSet: LineDataSet) {
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.lineWidth = 2F
        lineDataSet.circleRadius = 4f
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.valueTextSize = 8F
        //lineDataSet.circleHoleRadius = 4f
    }

    private fun lineDataSetChangeLineColor(lineDataSet: LineDataSet, color: Int) {
        lineDataSet.color = color
        lineDataSet.setCircleColor(color)
        lineDataSet.highLightColor = color
    }

    private fun graphInit(viewGraph: LineChart) {
        viewGraph.setNoDataTextColor(Color.RED)
        viewGraph.setNoDataText("No hay datos para graficar")
        viewGraph.isDoubleTapToZoomEnabled = false
        viewGraph.invalidate()
    }

    private fun graphConfig(viewGraph: LineChart, graphName: String, lineDataSets: MutableList<LineDataSet>, limitLines: HashSet<LimitLine>?) {
        viewGraph.description.text = graphName
        viewGraph.description.textSize = 10f

        viewGraph.data = LineData()
        for (element in lineDataSets) {
            viewGraph.data.addDataSet(element)
        }

        viewGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM// Set the xAxis position to bottom. Default is top
        viewGraph.xAxis.granularity = 1f// minimum axis-step (interval) is 1
        viewGraph.axisRight.isEnabled = false// Controlling right side of y axis
        viewGraph.axisLeft.granularity = 0.1f// Controlling left side of y axis

        viewGraph.setBorderWidth(2f)
        viewGraph.setDrawBorders(true)

        if (limitLines != null) {
            for (element in limitLines)
                viewGraph.axisLeft.addLimitLine(element)
        }

        viewGraph.animateX(2500, Easing.EasingOption.EaseOutSine)
        viewGraph.invalidate() // refresh
    }

    private fun graphAddLimit(sensor: Sensor, limit: Float, nameLabel: String) {
        val limitLine = LimitLine(limit, nameLabel)
        limitLine.lineWidth = 1.5f
        limitLine.enableDashedLine(15f, 15f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        limitLine.textSize = 8f
        sensor.limitLines.add(limitLine)
    }

    private fun graphTest(){
        graphInit(graphWet)
        graphInit(graphPh)

        val dataRequest = DataRequest()
        dataRequest.nameJSONObject = "data"

        val wet1 = Sensor()
        wet1.name = "h1"

        val wet2 = Sensor()
        wet2.name = "h2"

        val wet3 = Sensor()
        wet3.name = "h3"

        val wet4 = Sensor()
        wet4.name = "h4"

        val wet5 = Sensor()
        wet5.name = "h5"

        val ph = Sensor()
        ph.name = "ph"

        graphAddLimit(wet1, 90f, "Alto")
        graphAddLimit(wet1, 60f, "Mediano")
        graphAddLimit(wet1, 40f, "Bajo")

        for (i in 0..25) {
            wet1.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet1.entries.add(i, Entry(i.toFloat(), (wet1.sensedValues[i].value as Float)))

            wet2.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet2.entries.add(i, Entry(i.toFloat(), (wet2.sensedValues[i].value as Float)))

            wet3.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet3.entries.add(i, Entry(i.toFloat(), (wet3.sensedValues[i].value as Float)))

            wet4.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet4.entries.add(i, Entry(i.toFloat(), (wet4.sensedValues[i].value as Float)))

            wet5.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet5.entries.add(i, Entry(i.toFloat(), (wet5.sensedValues[i].value as Float)))

            ph.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            ph.entries.add(i, Entry(i.toFloat(), (ph.sensedValues[i].value as Float)))
        }

        wet1.lineDataSet = LineDataSet(wet1.entries, "h1")
        lineDataSetConfig(wet1.lineDataSet)
        lineDataSetChangeLineColor(wet1.lineDataSet, Color.YELLOW)

        wet2.lineDataSet = LineDataSet(wet2.entries, "h2")
        lineDataSetConfig(wet2.lineDataSet)
        lineDataSetChangeLineColor(wet2.lineDataSet, Color.CYAN)

        wet3.lineDataSet = LineDataSet(wet3.entries, "h3")
        lineDataSetConfig(wet3.lineDataSet)
        lineDataSetChangeLineColor(wet3.lineDataSet, Color.BLUE)

        wet4.lineDataSet = LineDataSet(wet4.entries, "h4")
        lineDataSetConfig(wet4.lineDataSet)
        lineDataSetChangeLineColor(wet4.lineDataSet, Color.GREEN)

        wet5.lineDataSet = LineDataSet(wet5.entries, "h5")
        lineDataSetConfig(wet5.lineDataSet)
        lineDataSetChangeLineColor(wet5.lineDataSet, Color.MAGENTA)

        ph.lineDataSet = LineDataSet(ph.entries, "ph")
        lineDataSetConfig(ph.lineDataSet)
        lineDataSetChangeLineColor(ph.lineDataSet, Color.RED)

        val lineDataSets: MutableList<LineDataSet> = mutableListOf()
        lineDataSets.add(0, wet1.lineDataSet)
        lineDataSets.add(1, wet2.lineDataSet)
        lineDataSets.add(2, wet3.lineDataSet)
        lineDataSets.add(3, wet4.lineDataSet)
        lineDataSets.add(4, wet5.lineDataSet)
        graphConfig(graphWet, "Grafica de Humedad", lineDataSets, wet1.limitLines)

        lineDataSets.clear()
        lineDataSets.add(0, ph.lineDataSet)
        graphConfig(graphPh, "Grafica de PH", lineDataSets, null)
    }

    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}