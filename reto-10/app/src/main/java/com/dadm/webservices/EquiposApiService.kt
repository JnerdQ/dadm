    package com.dadm.webservices

    import retrofit2.http.GET

    interface EquiposApiService {
        @GET("kgn2-si8r.json")
        suspend fun getEquipos(): List<Equipo>
    }