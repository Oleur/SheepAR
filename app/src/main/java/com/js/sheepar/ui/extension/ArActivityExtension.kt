package com.js.sheepar.ui.extension

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.widget.Toast

private const val MIN_OPENGL_VERSION = 3.0

fun Activity.checkIsSupportedDeviceOrFinish(): Boolean {
    val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

    if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
        Log.e("SheepAR", "Sceneform requires OpenGL ES 3.0 later")
        Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
        finish()
        return false
    }
    return true
}
