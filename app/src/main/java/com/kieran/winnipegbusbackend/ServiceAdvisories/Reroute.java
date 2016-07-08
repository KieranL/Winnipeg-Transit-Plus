package com.kieran.winnipegbusbackend.ServiceAdvisories;

import java.io.Serializable;
import java.util.List;

public class Reroute implements Serializable {
    private String heading;
    private List<String> instructions;

    public Reroute(String heading, List<String> instructions) {
        this.heading = heading;
        this.instructions = instructions;
    }

    public String getHeading() {
        return heading;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return heading + "\n" + instructions.toString() + "\n";
    }
}
