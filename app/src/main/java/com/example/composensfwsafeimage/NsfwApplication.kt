package com.example.composensfwsafeimage

import android.app.Application
import com.github.nikhilbiju67.composensfwsafeimage.NsfWBlocker

class NsfwApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            NsfWBlocker.initNSFW(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}