package com.desarollo.luisvillalobos.gardenkit.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.desarollo.luisvillalobos.gardenkit.R

class Graphs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs)

    }

    companion object {
        private val JSON_URL = "http://api.carriots.com/streams/?"
        private val API = "carriots.apikey"

        private val DEVICE = "device"
        private val AT_FROM = "&at_from="
        private val AT_TO = "&at_to="
        private val SORT = "&sort=at"
        private val ORDER = "&order=-1"

    }
}