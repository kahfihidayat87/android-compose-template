package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.PantiDto
import com.composetemplate.core.domain.model.Panti
import javax.inject.Inject

class PantiRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun getPantiList(): List<Panti> {
        val response: List<PantiDto> = repoCall { api.getPanti() }
        return response.map { it.toPanti() }
    }
}

private fun PantiDto.toPanti(): Panti {
    return Panti(
        id = id,
        nama = nama,
        alamat = alamat,
        jumlahSantri = jumlahSantri,
        direktur = direktur,
    )
}
