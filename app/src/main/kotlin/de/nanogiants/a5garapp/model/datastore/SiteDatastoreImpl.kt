package de.nanogiants.a5garapp.model.datastore

import android.content.Context
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.HwLocationType
import com.huawei.hms.site.api.model.HwLocationType.BANK
import com.huawei.hms.site.api.model.HwLocationType.CAFE
import com.huawei.hms.site.api.model.NearbySearchRequest
import com.huawei.hms.site.api.model.NearbySearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.NearbyPOI
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Tag
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SiteDatastoreImpl @Inject constructor(context: Context) : SiteDatastore {

  var searchService: SearchService = SearchServiceFactory.create(
    context,
    "CgB6e3x9gfqxB02ElADVb3qDLuH58sBFXY8KZPSgdV6zGn1c7LI/EHbu9fXDxtLTdwpSOHQGzaiv9mcrcJ1K9iaC"
  )

  override suspend fun getCafeSites(
    location: Coordinates,
    radiusInMeters: Int,
    limit: Int
  ): List<NearbyPOI> = getSite(location, radiusInMeters, limit, CAFE, Tag(1001, "cafe"))

  override suspend fun getBankSites(
    location: Coordinates,
    radiusInMeters: Int,
    limit: Int
  ): List<NearbyPOI> = getSite(location, radiusInMeters, limit, BANK, Tag(1002, "bank"))

  private suspend fun getSite(
    coordinates: Coordinates,
    radiusInMeters: Int,
    limit: Int,
    type: HwLocationType,
    tag: Tag
  ): List<NearbyPOI> {
    return suspendCoroutine { cont ->
      val request = NearbySearchRequest()

      request.location = Coordinate(coordinates.lat, coordinates.lng)
      request.radius = radiusInMeters
      request.hwPoiType = type
      request.language = "de"
      request.pageIndex = 1
      request.pageSize = limit

      searchService.nearbySearch(request, object : SearchResultListener<NearbySearchResponse> {
        override fun onSearchResult(response: NearbySearchResponse) {
          if (response.totalCount > 0) {
            val result = response.sites.map {

              Timber.d("heloe ${it.name} ${it.address.thoroughfare}, ${it.address.streetNumber}")

              NearbyPOI(
                id = -1,
                name = it.name,
                description = "",
                tags = listOf(tag),
                coordinates = Coordinates(it.location.lat, it.location.lng),
                imageUrls = (it.poi.photoUrls ?: arrayOf()).map { it },
                reviews = listOf(),
                rating = it.poi.rating.toFloat(),
                openingHours = listOf(),
                address = "${it.address.thoroughfare}, ${it.address.streetNumber}",
                distance = it.distance.toFloat(),
                url = it.poi.websiteUrl ?: ""
              )
            }

            cont.resume(result)
          } else {
            cont.resume(listOf())
          }
        }

        override fun onSearchError(status: SearchStatus) {
          Timber.e("Error ${status.errorCode} ${status.errorMessage}")
          cont.resumeWithException(Throwable(status.errorMessage))
        }
      })
    }
  }
}