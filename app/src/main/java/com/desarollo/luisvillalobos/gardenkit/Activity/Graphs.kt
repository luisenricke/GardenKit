package com.desarollo.luisvillalobos.gardenkit.Activity

import android.graphics.Color
import android.graphics.LinearGradient
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.desarollo.luisvillalobos.gardenkit.Controller.*
import com.desarollo.luisvillalobos.gardenkit.Model.*
import com.desarollo.luisvillalobos.gardenkit.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import kotlinx.android.synthetic.main.graphs.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Graphs : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var device: Device
    var listOfChoose = arrayOf("Seleccione una opción", "Hoy", "Ayer", "Una semana", "Dos semanas", "Escoger fechas")
    var itemChoosed = 0

    lateinit var calendarFrom: Calendar
    lateinit var calendarTo: Calendar

    companion object {
        private val JSON_URL = "http://api.altairsmartcore.com/streams/?"
        private val API = "Apikey"

        private val DEVICE = "device="
        private val AT_FROM = "&at_from="
        private val AT_TO = "&at_to="
        private val SORT = "&sort=at"
        private val ORDER = "&order=-1"

        const val REQUEST_TAG = "ALTAIRSMARTCORE_GRAPHS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs)
        setup()

        //getUsers()

        //graphTest()
        loadDataAllData()
    }

    private fun setup() {
        //Screen
        SetUpActivity.hiderActionBar(this)
        SetUpActivity.hideStatusBar(this)
        SetUpActivity.hideSoftKeyboard(this)

        //Spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfChoose)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_choose.adapter = spinnerAdapter

        //Listeners
        btn_home.setOnClickListener(this)
        spn_choose.onItemSelectedListener = this
        btn_date_to.setOnClickListener(this)
        btn_date_from.setOnClickListener(this)
        btn_send.setOnClickListener(this)

        //Graphs
        graphInit(graphWet)
        graphInit(graphPh)

        //GetExtras
        device = intent.getParcelableExtra(ListDevices.DEVICE_PARCEABLE_TAG)
    }


    //ClickListeners buttons implementation
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_home -> homeBtnClick()
            R.id.btn_date_to -> dateToBtnClick()
            R.id.btn_date_from -> dateFromBtnClick()
            R.id.btn_send -> sendBtnClick()
        }
    }

    private fun sendBtnClick() {
        when (itemChoosed) {
            0 -> {
                toast("Selecciona una opción valida")
            }
            1 -> {
            }
            2 -> {
            }
            3 -> {
            }
            4 -> {
            }
            5 -> {
            }
        }
    }

    private fun dateToBtnClick() {}
    private fun dateFromBtnClick() {}

    private fun homeBtnClick() {
        finish()
    }

    //ClickListener from Spinner implementation
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            btn_date_from.visibility = View.INVISIBLE
            btn_date_to.visibility = View.INVISIBLE
            itemChoosed = position
        } else {
            itemChoosed = position
        }

        if (listOfChoose[position] == "Escoger fechas") {
            btn_date_to.visibility = View.VISIBLE
            btn_date_from.visibility = View.VISIBLE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //ManageGraphs
    private fun loadDataByDates(date_from: Long?, date_to: Long?) {
        val dataRequest = DataRequest()
        dataRequest.nameJSONObject = "data"

        val emailDotDevice = "device@dev_bitbot_test.dev_bitbot_test"
        val apiKey = "d8a08f2ed52ec23463402769c3b0ccb6a8e4c6fa4bb6ea77f320cdbf553a2521"

        var URL_REQUEST: String? = null
        if (date_from == null && date_to == null) //All Data
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice
        if (date_to == null && date_from != null)
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice + AT_FROM + date_from + SORT + ORDER
        if (date_from != null && date_to != null)
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice + AT_FROM + date_from + AT_TO + date_to + SORT + ORDER


        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                URL_REQUEST,
                Response.Listener<String> { response ->

                    val wet1 = Sensor()
                    wet1.name = "Humedad1"

                    val wet2 = Sensor()
                    wet2.name = "Humedad2"

                    val wet3 = Sensor()
                    wet3.name = "Humedad3"

                    val wet4 = Sensor()
                    wet4.name = "Humedad4"

                    val wet5 = Sensor()
                    wet5.name = "Humedad5"

                    val ph = Sensor()
                    ph.name = "PH"

                    graphAddLimit(wet1, 90f, "Alto")
                    graphAddLimit(wet1, 60f, "Mediano")
                    graphAddLimit(wet1, 40f, "Bajo")

                    var json = JSONObject(response)
                    var results: JSONArray = json.getJSONArray("result")

                    for (element in 0 until results.length()) {
                        log(element.toString())
                        val aux = results.getJSONObject(element)
                        val data = aux.getJSONObject("data")

                        wet1.sensedValues.add(element, SensedValue(/*data.getString(wet1.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet1.entries.add(element, Entry(element.toFloat(), (wet1.sensedValues[element].value as Float)))
                        log("${wet1.name}:  (${wet1.entries.get(element).x}, ${wet1.entries.get(element).y})")

                        wet2.sensedValues.add(element, SensedValue(/*data.getString(wet2.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet2.entries.add(element, Entry(element.toFloat(), (wet2.sensedValues[element].value as Float)))
                        log("${wet2.name}:  (${wet2.entries.get(element).x}, ${wet2.entries.get(element).y})")

                        wet3.sensedValues.add(element, SensedValue(/*data.getString(wet3.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet3.entries.add(element, Entry(element.toFloat(), (wet3.sensedValues[element].value as Float)))
                        log("${wet3.name}:  (${wet3.entries.get(element).x}, ${wet3.entries.get(element).y})")

                        wet4.sensedValues.add(element, SensedValue(/*data.getString(wet4.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet4.entries.add(element, Entry(element.toFloat(), (wet4.sensedValues[element].value as Float)))
                        log("${wet4.name}:  (${wet4.entries.get(element).x}, ${wet4.entries.get(element).y})")

                        wet5.sensedValues.add(element, SensedValue(/*data.getString(wet5.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet5.entries.add(element, Entry(element.toFloat(), (wet5.sensedValues[element].value as Float)))
                        log("${wet5.name}:  (${wet5.entries.get(element).x}, ${wet5.entries.get(element).y})")

                        ph.sensedValues.add(element, SensedValue(/*data.getString(ph.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        ph.entries.add(element, Entry(element.toFloat(), (ph.sensedValues[element].value as Float)))
                        log("${ph.name}:  (${ph.entries.get(element).x}, ${ph.entries.get(element).y})")
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
/*
                    val lineDataSets: MutableList<LineDataSet> = mutableListOf()
                    lineDataSets.add(0, wet1.lineDataSet)
                    lineDataSets.add(1, wet2.lineDataSet)
                    lineDataSets.add(2, wet3.lineDataSet)
                    lineDataSets.add(3, wet4.lineDataSet)
                    lineDataSets.add(4, wet5.lineDataSet)
                    graphConfig(this.graphWet, "Grafica de Humedad", lineDataSets, wet1.limitLines)
*/

                    graphWet.description.text = "Grafica de Humedad"
                    graphWet.description.textSize = 10f
                    graphWet.data = LineData(wet1.lineDataSet, wet2.lineDataSet, wet3.lineDataSet, wet4.lineDataSet, wet5.lineDataSet)

                    graphWet.xAxis.position = XAxis.XAxisPosition.BOTTOM// Set the xAxis position to bottom. Default is top
                    graphWet.xAxis.granularity = 1f// minimum axis-step (interval) is 1
                    graphWet.axisRight.isEnabled = false// Controlling right side of y axis
                    graphWet.axisLeft.granularity = 0.1f// Controlling left side of y axis

                    graphWet.setBorderWidth(2f)
                    graphWet.setDrawBorders(true)

                    graphWet.animateX(2500, Easing.EaseOutSine)
                    graphWet.invalidate() // refresh

/*
                    lineDataSets.clear()
                    lineDataSets.add(0, ph.lineDataSet)
                    graphConfig(graphPh, "Grafica de PH", lineDataSets, null)
*/

                    graphPh.description.text = "Grafica de PH"
                    graphPh.description.textSize = 10f
                    graphPh.data = LineData(ph.lineDataSet)

                    graphPh.xAxis.position = XAxis.XAxisPosition.BOTTOM// Set the xAxis position to bottom. Default is top
                    graphPh.xAxis.granularity = 1f// minimum axis-step (interval) is 1
                    graphPh.axisRight.isEnabled = false// Controlling right side of y axis
                    graphPh.axisLeft.granularity = 0.1f// Controlling left side of y axis

                    graphPh.setBorderWidth(2f)
                    graphPh.setDrawBorders(true)

                    graphPh.animateX(2500, Easing.EaseOutSine)
                    graphPh.invalidate() // refresh
                },
                Response.ErrorListener {
                    log("Within internet")
                    toast("Verifique su conexión a Inteernet")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers[API] = apiKey
                return headers
            }

        }
        var request: RequestQueue = Volley.newRequestQueue(this)
        request.add(stringRequest)
    }

    private fun loadDataAllData() {
        val dataRequest = DataRequest()
        dataRequest.nameJSONObject = "data"

        val emailDotDevice = "device@dev_bitbot_test.dev_bitbot_test"
        val apiKey = "d8a08f2ed52ec23463402769c3b0ccb6a8e4c6fa4bb6ea77f320cdbf553a2521"

        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                JSON_URL + DEVICE + emailDotDevice,
                Response.Listener<String> { response ->

                    val wet1 = Sensor()
                    wet1.name = "Humedad1"

                    val wet2 = Sensor()
                    wet2.name = "Humedad2"

                    val wet3 = Sensor()
                    wet3.name = "Humedad3"

                    val wet4 = Sensor()
                    wet4.name = "Humedad4"

                    val wet5 = Sensor()
                    wet5.name = "Humedad5"

                    val ph = Sensor()
                    ph.name = "PH"

                    graphAddLimit(wet1, 90f, "Alto")
                    graphAddLimit(wet1, 60f, "Mediano")
                    graphAddLimit(wet1, 40f, "Bajo")

                    var json = JSONObject(response)
                    var results: JSONArray = json.getJSONArray("result")

                    for (element in 0 until results.length()) {
                        log(element.toString())
                        val aux = results.getJSONObject(element)
                        val data = aux.getJSONObject("data")

                        wet1.sensedValues.add(element, SensedValue(/*data.getString(wet1.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet1.entries.add(element, Entry(element.toFloat(), (wet1.sensedValues[element].value as Float)))
                        log("${wet1.name}:  (${wet1.entries.get(element).x}, ${wet1.entries.get(element).y})")

                        wet2.sensedValues.add(element, SensedValue(/*data.getString(wet2.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet2.entries.add(element, Entry(element.toFloat(), (wet2.sensedValues[element].value as Float)))
                        log("${wet2.name}:  (${wet2.entries.get(element).x}, ${wet2.entries.get(element).y})")

                        wet3.sensedValues.add(element, SensedValue(/*data.getString(wet3.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet3.entries.add(element, Entry(element.toFloat(), (wet3.sensedValues[element].value as Float)))
                        log("${wet3.name}:  (${wet3.entries.get(element).x}, ${wet3.entries.get(element).y})")

                        wet4.sensedValues.add(element, SensedValue(/*data.getString(wet4.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet4.entries.add(element, Entry(element.toFloat(), (wet4.sensedValues[element].value as Float)))
                        log("${wet4.name}:  (${wet4.entries.get(element).x}, ${wet4.entries.get(element).y})")

                        wet5.sensedValues.add(element, SensedValue(/*data.getString(wet5.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        wet5.entries.add(element, Entry(element.toFloat(), (wet5.sensedValues[element].value as Float)))
                        log("${wet5.name}:  (${wet5.entries.get(element).x}, ${wet5.entries.get(element).y})")

                        ph.sensedValues.add(element, SensedValue(/*data.getString(ph.name).toFloat()*/(20..100).random().toFloat(), aux.getLong("at")))
                        ph.entries.add(element, Entry(element.toFloat(), (ph.sensedValues[element].value as Float)))
                        log("${ph.name}:  (${ph.entries.get(element).x}, ${ph.entries.get(element).y})")
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
/*
                    val lineDataSets: MutableList<LineDataSet> = mutableListOf()
                    lineDataSets.add(0, wet1.lineDataSet)
                    lineDataSets.add(1, wet2.lineDataSet)
                    lineDataSets.add(2, wet3.lineDataSet)
                    lineDataSets.add(3, wet4.lineDataSet)
                    lineDataSets.add(4, wet5.lineDataSet)
                    graphConfig(this.graphWet, "Grafica de Humedad", lineDataSets, wet1.limitLines)
*/

                    graphWet.description.text = "Grafica de Humedad"
                    graphWet.description.textSize = 10f
                    graphWet.data = LineData(wet1.lineDataSet, wet2.lineDataSet, wet3.lineDataSet, wet4.lineDataSet, wet5.lineDataSet)

                    graphWet.xAxis.position = XAxis.XAxisPosition.BOTTOM// Set the xAxis position to bottom. Default is top
                    graphWet.xAxis.granularity = 1f// minimum axis-step (interval) is 1
                    graphWet.axisRight.isEnabled = false// Controlling right side of y axis
                    graphWet.axisLeft.granularity = 0.1f// Controlling left side of y axis

                    graphWet.setBorderWidth(2f)
                    graphWet.setDrawBorders(true)

                    graphWet.animateX(2500, Easing.EaseOutSine)
                    graphWet.invalidate() // refresh

/*
                    lineDataSets.clear()
                    lineDataSets.add(0, ph.lineDataSet)
                    graphConfig(graphPh, "Grafica de PH", lineDataSets, null)
*/

                    graphPh.description.text = "Grafica de PH"
                    graphPh.description.textSize = 10f
                    graphPh.data = LineData(ph.lineDataSet)

                    graphPh.xAxis.position = XAxis.XAxisPosition.BOTTOM// Set the xAxis position to bottom. Default is top
                    graphPh.xAxis.granularity = 1f// minimum axis-step (interval) is 1
                    graphPh.axisRight.isEnabled = false// Controlling right side of y axis
                    graphPh.axisLeft.granularity = 0.1f// Controlling left side of y axis

                    graphPh.setBorderWidth(2f)
                    graphPh.setDrawBorders(true)

                    graphPh.animateX(2500, Easing.EaseOutSine)
                    graphPh.invalidate() // refresh
                },
                Response.ErrorListener {
                    log("That didn't work!")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                headers.put(API, apiKey)
                return headers
            }

        }
        var request: RequestQueue = Volley.newRequestQueue(this)
        request.add(stringRequest)
    }

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
        viewGraph.isDoubleTapToZoomEnabled = true
        viewGraph.invalidate()
    }

    private fun graphConfig(viewGraph: LineChart, graphName: String, lineDataSets: MutableList<LineDataSet>, limitLines: HashSet<LimitLine>?) { //TODO:Check how to pass instance of graph

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

        viewGraph.animateX(2500, Easing.EaseOutSine)
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

    private fun graphTest() {


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
            log("${wet1.name}:  (${wet1.entries.get(i).x}, ${wet1.entries.get(i).y})")

            wet2.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet2.entries.add(i, Entry(i.toFloat(), (wet2.sensedValues[i].value as Float)))
            log("${wet2.name}:  (${wet2.entries.get(i).x}, ${wet2.entries.get(i).y})")

            wet3.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet3.entries.add(i, Entry(i.toFloat(), (wet3.sensedValues[i].value as Float)))
            log("${wet3.name}:  (${wet2.entries.get(i).x}, ${wet3.entries.get(i).y})")

            wet4.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet4.entries.add(i, Entry(i.toFloat(), (wet4.sensedValues[i].value as Float)))
            log("${wet4.name}:  (${wet4.entries.get(i).x}, ${wet4.entries.get(i).y})")

            wet5.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            wet5.entries.add(i, Entry(i.toFloat(), (wet5.sensedValues[i].value as Float)))
            log("${wet5.name}:  (${wet4.entries.get(i).x}, ${wet5.entries.get(i).y})")

            ph.sensedValues.add(i, SensedValue((20..100).random().toFloat(), 1556582400L))
            ph.entries.add(i, Entry(i.toFloat(), (ph.sensedValues[i].value as Float)))
            log("${ph.name}:  (${ph.entries.get(i).x}, ${ph.entries.get(i).y})")
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
        //graphConfig(graphWet, "Grafica de Humedad", lineDataSets, wet1.limitLines)

        lineDataSets.clear()
        lineDataSets.add(0, ph.lineDataSet)
        //graphConfig(graphPh, "Grafica de PH", lineDataSets, null)
    }

    //Extras


    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}