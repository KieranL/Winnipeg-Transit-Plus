package com.kieran.winnipegbus.enums;

public enum TimeStatuses {
    Ok("Ok"),
    Late("Late"),
    Early("Early");

    public String status;

    TimeStatuses(String status) {
        this.status = status;
    }
}
