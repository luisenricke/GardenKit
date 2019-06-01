package com.desarollo.luisvillalobos.gardenkit.Model

import android.graphics.Color
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import kotlin.collections.HashSet

class DataRequest {
    var nameJSONObject: String? = null
}

class Sensor {
    lateinit var name: String
    lateinit var lineDataSet: LineDataSet
    var limitLines: HashSet<LimitLine> = hashSetOf()
    var entries: MutableList<Entry> = mutableListOf()
    var sensedValues: MutableList<SensedValue> = mutableListOf()
}

class SensedValue {
    var value: Any? = null
    var date: Date? = null

    constructor(value: Any?, unixTime: Long?) {
        this.value = value
        if (unixTime != null) {
            this.date = Date(unixTime * 1000L)
        }
    }
}

