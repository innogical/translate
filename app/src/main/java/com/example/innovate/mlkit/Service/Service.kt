package com.example.innovate.mlkit.Service

import android.app.PendingIntent.getService
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.GsonBuilder


public class Service {

    init {
        FuelManager.instance.basePath = "url"
        Fuel.testMode { timeout = 15000 }
    }

}