package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PantiDto(
    val id: Int = 0,
    val nama: String = "",
    val alamat: String = "",
    val jumlahSantri: Int = 0,
    val direktur: String = "",
)
