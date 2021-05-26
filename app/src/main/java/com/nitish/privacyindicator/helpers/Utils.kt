package com.nitish.privacyindicator.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.nitish.privacyindicator.models.IndicatorOpacity


fun Activity.goToActivity(newActivity: Class<*>?) {
    this.startActivity(Intent(this, newActivity))
}

fun Activity.openBrowser(URL: String) {
    this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL)))
}

fun Activity.openSharingScreen(text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "plain/text"
    }
    this.startActivity(intent)
}

fun View.updateSize(size: Int){
    requestLayout()
    layoutParams.height = size
    layoutParams.width = size
}

fun View.setViewTint(hexColor: String) {

}

fun View.updateOpacity(opacity: Float) {
    requestLayout()
    alpha = opacity
}