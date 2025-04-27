package com.wheel.carx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WheelSegment(
    val text: String,
    val color: Int,
    val isDeleted: Boolean = false
) : Parcelable
