package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;

public abstract class Segment {
    StopTime start;
    StopTime end;
    Location from;
    Location to;
}
