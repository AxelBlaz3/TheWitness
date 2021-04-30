package com.wielabs.thewitness.util

fun Boolean.toInt() =
    if (this)
        1
    else
        0