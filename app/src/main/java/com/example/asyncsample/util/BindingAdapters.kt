package com.example.asyncsample.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibility")
fun invisibility(v: View, show: Boolean) {
    v.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("disabled")
fun disabled(v: View, isDisabled: Boolean) {
    v.isEnabled = !isDisabled
}
