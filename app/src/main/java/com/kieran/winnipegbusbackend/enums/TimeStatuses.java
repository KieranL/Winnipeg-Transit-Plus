package com.kieran.winnipegbusbackend.enums;

public enum TimeStatuses {
    Ok("Ok"),
    Late("Late"),
    Early("Early");

    public String status;

    TimeStatuses(String status) {
        this.status = status;
    }
}
