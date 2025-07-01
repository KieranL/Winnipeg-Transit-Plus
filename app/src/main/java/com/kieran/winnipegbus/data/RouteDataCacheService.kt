package com.kieran.winnipegbus.data

import android.content.Context
import androidx.room.Room
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteBadge
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

class RouteDataCacheService private constructor(ctx: Context){
    private val db = Room.databaseBuilder(
        ctx,
        RouteCacheDB::class.java, "route_data_cache"
    ).build()

    companion object {
        private var instance: RouteDataCacheService? = null
        private var staleCache: HashMap<RouteIdentifier, RouteDataCache> = HashMap<RouteIdentifier, RouteDataCache>()
        private var internalCache: HashMap<RouteIdentifier, RouteDataCache> = HashMap<RouteIdentifier, RouteDataCache>()
        @Synchronized
        fun getInstance(ctx: Context) = instance
            ?: RouteDataCacheService(ctx.applicationContext)
    }

    fun hydrateFromCache(routeIdentifier: RouteIdentifier, agencyId: Long) {
        var data = internalCache.get(routeIdentifier)

        if (data == null)
            data = staleCache.get(routeIdentifier)

        if (data == null)
            return

        val badge = WinnipegTransitRouteBadge(data.textColour, data.backgroundColour, data.borderColour)

        routeIdentifier.setBadge(badge)
        internalCache.put(routeIdentifier, data)
    }

    fun upsert(routeIdentifier: RouteIdentifier, agencyId: Long) {
        val existing = internalCache.get(routeIdentifier)

        if (existing == null && routeIdentifier.getRouteBadge() != null) {
            val cacheEntry = RouteDataCache(
                routeIdentifier.toString(),
                routeIdentifier.getRouteBadge()!!.getTextColour(),
                routeIdentifier.getRouteBadge()!!.getBackgroundColour(),
                routeIdentifier.getRouteBadge()!!.getBorderColour(),
                agencyId
            )

            db.routeDataCacheDAO().upsert(cacheEntry)
            internalCache.put(routeIdentifier, cacheEntry)
        }
    }

    fun load(agencyId: Long) {
        val saved = db.routeDataCacheDAO().all(agencyId)

        for (data in saved) {
            val badge = WinnipegTransitRouteBadge(data.textColour, data.backgroundColour, data.borderColour)
            val identifier = WinnipegTransitRouteIdentifier(data.routeIdentifier, badge)
            staleCache.put(identifier, data)
        }
    }
}