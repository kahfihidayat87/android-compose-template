package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.OrderCreateRequest
import com.composetemplate.core.data.network.dtos.OrderDto
import com.composetemplate.core.domain.model.Order
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun createOrder(request: OrderCreateRequest): Order {
        val dto: OrderDto = repoCall { api.createOrder(request) }
        return dto.toOrder()
    }

    suspend fun getMyOrders(): List<Order> {
        val list: List<OrderDto> = repoCall { api.getMyOrders() }
        return list.map { it.toOrder() }
    }

    suspend fun getMyOrder(id: Int): Order {
        val dto: OrderDto = repoCall { api.getMyOrder(id) }
        return dto.toOrder()
    }
}

private fun OrderDto.toOrder(): Order {
    return Order(
        id = id,
        orderNumber = orderNumber,
        subtotal = subtotal,
        shippingCost = shippingCost,
        discountAmount = discountAmount,
        total = total,
        status = status,
        shippingAddress = shippingAddress,
        shippingPhone = shippingPhone,
        createdAt = createdAt,
        resi = resi,
    )
}
