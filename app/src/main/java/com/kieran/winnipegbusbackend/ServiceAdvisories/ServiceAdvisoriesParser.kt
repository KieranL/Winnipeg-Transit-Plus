package com.kieran.winnipegbusbackend.ServiceAdvisories

import com.kieran.winnipegbusbackend.StopTime

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

object ServiceAdvisoriesParser {

    val BODY_SECTIONS_REGEX = "\\n\\n"
    val BODY_SUBSECTION_REGEX = "[^\\*]\\*{1} "
    val REMOVE_ASTERISK_REGEX = "\\*"
    val TITLE_TAG = "title"
    val BODY_TAG = "body"
    val SERVICE_ADVISORY_TAG = "service-advisories"
    val AFFECTED_STOP_REGEX = "\\*\\*"
    val REROUTE_REGEX = "\n\\*\\*"
    val UPDATED_AT_TAG = "updated-at"

    fun parseAdvisories(jsonObject: JSONObject): List<ServiceAdvisory> {
        val advisories = ArrayList<ServiceAdvisory>()

        try {
            val nodes = jsonObject.getJSONArray(SERVICE_ADVISORY_TAG)

            for (i in 0 until nodes.length())
                advisories.add(getServiceAdvisory(nodes.getJSONObject(i)))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Collections.sort(advisories)
        return advisories
    }

    @Throws(JSONException::class)
    private fun getServiceAdvisory(node: JSONObject): ServiceAdvisory {
        val title = node.getString(TITLE_TAG)
        val body = node.getString(BODY_TAG)
        val bodySections = body.split(BODY_SECTIONS_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val header = bodySections[0]
        val affectedStopsData: Array<String>
        val reRoutesData: Array<String>

        if (bodySections.size == 3) {
            affectedStopsData = arrayOf()
            reRoutesData = bodySections[2].split(BODY_SUBSECTION_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        } else {
            affectedStopsData = bodySections[1].split(BODY_SUBSECTION_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            reRoutesData = bodySections[3].split(BODY_SUBSECTION_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }

        return ServiceAdvisory(title, header, getAffectedStops(affectedStopsData), getReRoutes(reRoutesData), StopTime.convertStringToStopTime(node.getString(UPDATED_AT_TAG))!!)
    }

    private fun getAffectedStops(nodes: Array<String>): List<AffectedStop> {
        val stops = ArrayList<AffectedStop>()

        for (node in nodes) {
            val s = node.split(AFFECTED_STOP_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (s.size > 1)
                stops.add(AffectedStop(s[0].replace(REMOVE_ASTERISK_REGEX.toRegex(), "").trim { it <= ' ' }, s[1]))
            else
                stops.add(AffectedStop(s[0].replace(REMOVE_ASTERISK_REGEX.toRegex(), "").trim { it <= ' ' }, ""))
        }

        return stops
    }

    private fun getReRoutes(nodes: Array<String>): List<Reroute> {
        val reroutes = ArrayList<Reroute>()

        for (node in nodes) {
            val s = node.split(REROUTE_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sub = arrayOfNulls<String>(s.size - 1)
            System.arraycopy(s, 1, sub, 0, s.size - 1)

            reroutes.add(Reroute(s[0].replace(REMOVE_ASTERISK_REGEX.toRegex(), "").trim { it <= ' ' }, Arrays.asList<String>(*sub)))
        }

        return reroutes
    }
}
