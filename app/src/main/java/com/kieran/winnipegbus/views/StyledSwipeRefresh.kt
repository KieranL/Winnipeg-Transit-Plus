package com.kieran.winnipegbus.views

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet

import com.kieran.winnipegbus.R

class StyledSwipeRefresh : androidx.swiperefreshlayout.widget.SwipeRefreshLayout {
    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    fun initialize() {
        setColorSchemeResources(R.color.rt_blue, R.color.rt_red)
    }
}
