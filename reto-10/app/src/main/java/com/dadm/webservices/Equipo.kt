package com.dadm.webservices


import com.google.gson.annotations.SerializedName

data class Equipo(
    @SerializedName("no") val no: String, // JSON uses String for "no"
    @SerializedName("equipo") val equipo: String,
    @SerializedName("memoria") val memoria: String,
    @SerializedName("procesador") val procesador: String,
    @SerializedName("discos") val discos: String,
    @SerializedName("sistema_operativo") val sistemaOperativo: String,
    @SerializedName("version") val version: String,
    @SerializedName("software_instalado") val softwareInstalado: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("version_ip") val versionIp: String,
    @SerializedName("oficina_en_la_que_se_encuentra_el_dispositivo") val oficina: String
)