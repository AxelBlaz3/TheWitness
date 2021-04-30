package com.wielabs.thewitness.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter(value = ["glideSrc", "glideCircleCrop"], requireAll = false)
fun setGlideSrc(
    imageView: ImageView,
    newUrl: String?,
    circleCrop: Boolean = false
) {
    newUrl?.let {
        if (circleCrop)
            Glide.with(imageView.context).load(newUrl).circleCrop().into(imageView)
        else
            Glide.with(imageView.context).load(newUrl).into(imageView)
    }
}