package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.ShippingItemRequest
import com.composetemplate.core.data.network.ShippingRatesRequest
import com.composetemplate.core.data.network.dtos.ShippingRateDto
import com.composetemplate.core.data.network.dtos.ShippingRatesResponse
import com.composetemplate.core.domain.model.ShippingRate
import javax.inject.Inject

class ShippingRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun getRates(
        destinationPostalCode: String,
        items: List<ShippingItemRequest>
    ): List<ShippingRate> {
        val response: ShippingRatesResponse = repoCall {
            api.postShippingRates(ShippingRatesRequest(destinationPostalCode, items))
        }
        return response.rates.map { it.toShippingRate() }
    }
}

private fun ShippingRateDto.toShippingRate(): ShippingRate {
    return ShippingRate(
        courierCode = courierCode,
        courierName = courierName,
        serviceCode = serviceCode,
        serviceName = serviceName,
        price = price,
        estimatedDays = estimatedDays,
    )
}
