package com.kieran.winnipegbusbackend;

import java.util.List;

public class URLParameter {
    private static final String FORMAT = "%s=%s";
    private String key;
    private String value;

    public URLParameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public URLParameter(String key, List<Integer> numbers) {
        this.key = key;
        value = "";

        for (int i = 0; i < numbers.size(); i++) {
            value += Integer.toString(numbers.get(i));
            if (i < numbers.size() - 1)
                value += ",";
        }
    }

    @Override
    public String toString() {
        return String.format(FORMAT, key, value);
    }
}
