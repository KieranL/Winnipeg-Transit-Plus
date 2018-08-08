package com.kieran.winnipegbus.views

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet

import com.kieran.winnipegbus.R

class StyledSwipeRefresh : SwipeRefreshLayout {
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
