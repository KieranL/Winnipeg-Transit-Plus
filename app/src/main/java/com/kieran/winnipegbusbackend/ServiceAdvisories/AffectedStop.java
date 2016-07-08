package com.kieran.winnipegbusbackend.ServiceAdvisories;

import java.io.Serializable;

public class AffectedStop implements Serializable {
    private String affectedStop;
    private String affectedRoutes;

    public AffectedStop(String affectedStop, String affectedRoutes) {
        this.affectedStop = affectedStop;
        this.affectedRoutes = affectedRoutes;
    }

    public String getAffectedStop() {
        return affectedStop;
    }

    public String getAffectedRoutes() {
        return affectedRoutes;
    }

    @Override
    public String toString() {
        return affectedStop + " " + affectedRoutes + "\n";
    }
}
