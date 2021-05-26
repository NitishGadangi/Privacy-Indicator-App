package com.nitish.privacyindicator.models

import android.view.Gravity

enum class IndicatorPosition(val layoutGravity: Int) {
    TOP_LEFT(Gravity.TOP or Gravity.START),
    TOP_RIGHT(Gravity.TOP or Gravity.END),
    BOTTOM_RIGHT(Gravity.BOTTOM or Gravity.END),
    BOTTOM_LEFT(Gravity.TOP or Gravity.START),
    TOP_CENTER(Gravity.TOP or Gravity.CENTER),
    BOTTOM_CENTER(Gravity.BOTTOM or Gravity.CENTER)
}