package com.kieran.winnipegbusbackend;

public class LoadResult {
    private Object result;
    private Exception exception;

    public LoadResult(Object result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
