package com.kieran.winnipegbusbackend

class URLParameter {
    private var key: String? = null
    private var value: String? = null

    constructor(key: String, value: String) {
        this.key = key
        this.value = value
    }

    constructor(key: String, numbers: List<Int>) {
        this.key = key
        value = ""

        for (i in numbers.indices) {
            value += Integer.toString(numbers[i])
            if (i < numbers.size - 1)
                value += ","
        }
    }

    override fun toString(): String {
        return String.format(FORMAT, key, value)
    }

    companion object {
        private val FORMAT = "%s=%s"
    }
}