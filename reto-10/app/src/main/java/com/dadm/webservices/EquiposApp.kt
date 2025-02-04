package com.dadm.webservices

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EquiposApp(modifier: Modifier = Modifier) {
    val viewModel: EquiposViewModel = viewModel()
    val equipos by viewModel.equipos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar equipos...") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(
                equipos.filter {
                    (it.equipo ?: "").contains(searchQuery, ignoreCase = true) ||
                            (it.oficina ?: "")
                                .contains(searchQuery, ignoreCase = true)
                }
            ) { equipo ->
                EquipoCard(equipo = equipo)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EquipoCard(equipo: Equipo) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = equipo.equipo ?: "No data available",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Memoria: ${equipo.memoria ?: "N/A"}")
            Text("Procesador: ${equipo.procesador ?: "N/A"}")
            Text("Ubicación: ${equipo.oficina ?: "N/A"}")
            Text("Sistema Operativo: ${equipo.sistemaOperativo ?: "N/A"} ${equipo.version ?: ""}")
        }
    }
}
