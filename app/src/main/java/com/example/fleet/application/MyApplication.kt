package com.example.fleet.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.fleet.utils.FontStyle


class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
        MultiDex.install(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")
    }


    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
    }


}