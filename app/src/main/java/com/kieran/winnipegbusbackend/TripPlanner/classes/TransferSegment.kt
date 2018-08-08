package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONObject

class TransferSegment(tripParameters: TripParameters, segment: JSONObject) : Segment(tripParameters, segment)
