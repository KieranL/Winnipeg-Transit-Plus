package com.kieran.winnipegbusbackend;

public class LoadResult<T> {
    private T result;
    private Exception exception;

    public LoadResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
