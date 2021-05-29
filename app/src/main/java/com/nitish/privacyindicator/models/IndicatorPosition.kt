package com.nitish.privacyindicator.models

import android.view.Gravity

enum class IndicatorPosition(val layoutGravity: Int, val vertical: Int, val horizontal: Int) {
    TOP_LEFT(Gravity.TOP or Gravity.START, 0, 0),
    TOP_CENTER(Gravity.TOP or Gravity.CENTER, 0, 1),
    TOP_RIGHT(Gravity.TOP or Gravity.END, 0, 2),
    BOTTOM_LEFT(Gravity.TOP or Gravity.START, 1, 0),
    BOTTOM_CENTER(Gravity.BOTTOM or Gravity.CENTER, 1, 1),
    BOTTOM_RIGHT(Gravity.BOTTOM or Gravity.END, 1, 2);

    companion object {
        fun getIndicatorPosition(vertical: Int, horizontal: Int): IndicatorPosition {
            return when {
                vertical == 0 && horizontal == 0 -> TOP_LEFT
                vertical == 0 && horizontal == 1 -> TOP_CENTER
                vertical == 0 && horizontal == 2 -> TOP_RIGHT
                vertical == 1 && horizontal == 0 -> BOTTOM_LEFT
                vertical == 1 && horizontal == 1 -> BOTTOM_CENTER
                vertical == 1 && horizontal == 2 -> BOTTOM_RIGHT
                else -> TOP_RIGHT
            }
        }
    }
}