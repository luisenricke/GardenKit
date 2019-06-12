package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.DatePickerDialog
import android.graphics.Color
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
import kotlinx.android.synthetic.main.graphs.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.absoluteValue

class Graphs : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var device: Device
    var listOfChoose = arrayOf("Seleccione una opción", "Hoy", "Ayer", "Una semana", "Dos semanas", "Un mes", "Escoger fechas")
    var itemChoosed = 0

    lateinit var calendarInstance: Calendar
    val DATEF: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    var _year: Int = 0
    var _month: Int = 0
    var _day: Int = 0
    var _hour: Int = 0
    var _minute: Int = 0
    var _second: Int = 0

    var datePicker: DatePickerDialog? = null
    var longTo: Long = 0
    var longFrom: Long = 0

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

        //Date
        calendarInstance = Calendar.getInstance()
        calendarInstance.time = Date(System.currentTimeMillis())
        calendarInstance.timeZone = TimeZone.getTimeZone("CDT")
        log(DATEF.format(calendarInstance.time))
        _year = calendarInstance.get(Calendar.YEAR)
        _month = calendarInstance.get(Calendar.MONTH)
        _day = calendarInstance.get(Calendar.DAY_OF_MONTH)
        _hour = calendarInstance.get(Calendar.HOUR_OF_DAY)
        _minute = calendarInstance.get(Calendar.MINUTE)
        _second = calendarInstance.get(Calendar.SECOND)
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
        calendarInstance.time = Date(System.currentTimeMillis())
        when (itemChoosed) {
            0 -> {
                toast("Selecciona una opción valida")
            }
            1 -> {
                calendarInstance.time = DateOperations.clearTime(calendarInstance.time)
                longFrom = calendarInstance.time.time
                loadDataByDates(longFrom, 0)
            }
            2 -> {
                calendarInstance.time = DateOperations.subDay(DateOperations.clearTime(calendarInstance.time), 1)
                longFrom = calendarInstance.time.time
                loadDataByDates(longFrom, 0)
            }
            3 -> {
                calendarInstance.time = DateOperations.subDay(DateOperations.clearTime(calendarInstance.time), 7)
                longFrom = calendarInstance.time.time
                loadDataByDates(longFrom, 0)
            }
            4 -> {
                calendarInstance.time = DateOperations.subDay(DateOperations.clearTime(calendarInstance.time), 14)
                longFrom = calendarInstance.time.time
                loadDataByDates(longFrom, 0)
            }
            5 -> {
                calendarInstance.time = DateOperations.subMonth(DateOperations.clearTime(calendarInstance.time), 1)
                longFrom = calendarInstance.time.time
                loadDataByDates(longFrom, 0)
            }
            6 -> {
                if (longFrom < longTo)
                    loadDataByDates(longFrom, longTo)
                else
                    toast("Error en las fechas, vuelve a ingresarlos")
            }
        }
    }

    private fun dateFromBtnClick() {
        calendarInstance.time = Date(System.currentTimeMillis())
        log(DATEF.format(calendarInstance.time))
        datePicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    calendarInstance.set(Calendar.YEAR, year)
                    calendarInstance.set(Calendar.MONTH, month)
                    calendarInstance.set(Calendar.DAY_OF_MONTH, day)
                    calendarInstance.time = DateOperations.clearTime(calendarInstance.time)
                    calendarInstance.set(Calendar.HOUR_OF_DAY, calendarInstance.get(Calendar.HOUR_OF_DAY))
                    calendarInstance.set(Calendar.MINUTE, calendarInstance.get(Calendar.MINUTE))
                    calendarInstance.set(Calendar.SECOND, calendarInstance.get(Calendar.SECOND))
                    longFrom = calendarInstance.time.time
                    log(DATEF.format(longFrom) + " - " + calendarInstance.time.toString())
                }, _year, _month, _day)
        datePicker!!.datePicker.maxDate = calendarInstance.timeInMillis
        datePicker?.show()
        datePicker = null
    }

    private fun dateToBtnClick() {
        calendarInstance.time = Date(System.currentTimeMillis())
        log(DATEF.format(calendarInstance.time))
        datePicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    calendarInstance.set(Calendar.YEAR, year)
                    calendarInstance.set(Calendar.MONTH, month)
                    calendarInstance.set(Calendar.DAY_OF_MONTH, day)
                    calendarInstance.time = DateOperations.getEnd(calendarInstance.time)
                    calendarInstance.set(Calendar.HOUR_OF_DAY, calendarInstance.get(Calendar.HOUR_OF_DAY))
                    calendarInstance.set(Calendar.MINUTE, calendarInstance.get(Calendar.MINUTE))
                    calendarInstance.set(Calendar.SECOND, calendarInstance.get(Calendar.SECOND))
                    longTo = calendarInstance.time.time
                    log(DATEF.format(longTo) + " - " + calendarInstance.time.toString())
                }, _year, _month, _day)
        datePicker!!.datePicker.maxDate = calendarInstance.timeInMillis
        datePicker?.show()
        datePicker = null
    }

    private fun homeBtnClick() {
        finish()
    }


    //ClickListener from Spinner implementation
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemChoosed = position
        btn_date_from.visibility = View.INVISIBLE
        btn_date_to.visibility = View.INVISIBLE

        if (listOfChoose[position] == "Escoger fechas") {
            btn_date_to.visibility = View.VISIBLE
            btn_date_from.visibility = View.VISIBLE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //ManageGraphs
    private fun loadDataByDates(date_from: Long, date_to: Long) {
        val dataRequest = DataRequest()
        dataRequest.nameJSONObject = "data"
        val emailDotDevice = device.device_request
        val apiKey = device.apikey_request

        var URL_REQUEST: String? = null
        if (date_from == 0L && date_to == 0L) //All Data
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice
        if (date_from != 0L && date_to == 0L)
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice + AT_FROM + (date_from / 1000) + SORT + ORDER
        if (date_from != 0L && date_to != 0L)
            URL_REQUEST = JSON_URL + DEVICE + emailDotDevice + AT_FROM + (date_from / 1000) + AT_TO + (date_to / 1000) + SORT + ORDER

        var stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                URL_REQUEST,
                Response.Listener<String> { response ->
                    try {
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

                        var limitLine = LimitLine(90F, "Alto")
                        limitLine.lineWidth = 1.5f
                        limitLine.enableDashedLine(15f, 15f, 0f)
                        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        limitLine.textSize = 8f
                        wet1.limitLines.add(limitLine)

                        limitLine = LimitLine(60f, "Mediano")
                        limitLine.lineWidth = 1.5f
                        limitLine.enableDashedLine(15f, 15f, 0f)
                        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        limitLine.textSize = 8f
                        wet1.limitLines.add(limitLine)

                        limitLine = LimitLine(40f, "Bajo")
                        limitLine.lineWidth = 1.5f
                        limitLine.enableDashedLine(15f, 15f, 0f)
                        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        limitLine.textSize = 8f
                        wet1.limitLines.add(limitLine)

                        var json = JSONObject(response)
                        var results: JSONArray = json.getJSONArray("result")

                        for (element in 0 until results.length()) {
                            log(element.toString())
                            val aux = results.getJSONObject(element)
                            val data = aux.getJSONObject("data")

                            wet1.sensedValues.add(element, SensedValue(data.getString(wet1.name).toFloat(), aux.getLong("at")))
                            wet1.entries.add(element, Entry(element.toFloat(), (wet1.sensedValues[element].value as Float)))
                            log("${wet1.name}:  (${wet1.entries.get(element).x}, ${wet1.entries.get(element).y})")

                            wet2.sensedValues.add(element, SensedValue(data.getString(wet2.name).toFloat(), aux.getLong("at")))
                            wet2.entries.add(element, Entry(element.toFloat(), (wet2.sensedValues[element].value as Float)))
                            log("${wet2.name}:  (${wet2.entries.get(element).x}, ${wet2.entries.get(element).y})")

                            wet3.sensedValues.add(element, SensedValue(data.getString(wet3.name).toFloat(), aux.getLong("at")))
                            wet3.entries.add(element, Entry(element.toFloat(), (wet3.sensedValues[element].value as Float)))
                            log("${wet3.name}:  (${wet3.entries.get(element).x}, ${wet3.entries.get(element).y})")

                            wet4.sensedValues.add(element, SensedValue(data.getString(wet4.name).toFloat(), aux.getLong("at")))
                            wet4.entries.add(element, Entry(element.toFloat(), (wet4.sensedValues[element].value as Float)))
                            log("${wet4.name}:  (${wet4.entries.get(element).x}, ${wet4.entries.get(element).y})")

                            wet5.sensedValues.add(element, SensedValue(data.getString(wet5.name).toFloat(), aux.getLong("at")))
                            wet5.entries.add(element, Entry(element.toFloat(), (wet5.sensedValues[element].value as Float)))
                            log("${wet5.name}:  (${wet5.entries.get(element).x}, ${wet5.entries.get(element).y})")

                            ph.sensedValues.add(element, SensedValue(data.getString(ph.name).toFloat(), aux.getLong("at")))
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
                    } catch (e: Exception) {
                        log("${e.message}")
                        toast("Verifique los datos del dispositivo o si tiene Internet")
                    }
                },
                Response.ErrorListener {
                    toast("Verifique los datos del dispositivo")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers[API] = apiKey!!
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

    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}