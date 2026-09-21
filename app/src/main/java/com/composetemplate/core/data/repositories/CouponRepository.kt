package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.CouponValidateRequest
import com.composetemplate.core.data.network.dtos.CouponValidateResponse
import javax.inject.Inject

class CouponRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun validate(code: String, subtotal: Int): CouponValidateResponse {
        return repoCall {
            api.validateCoupon(CouponValidateRequest(code, subtotal))
        }
    }
}
