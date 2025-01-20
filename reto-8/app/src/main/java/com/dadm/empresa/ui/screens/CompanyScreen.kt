package com.dadm.empresa.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dadm.empresa.data.SoftwareCompany
import com.dadm.empresa.ui.components.CompanyCard
import com.dadm.empresa.ui.components.CompanyDialog
import com.dadm.empresa.ui.screens.ui.theme.EmpresaTheme
import com.dadm.empresa.ui.viewmodel.CompanyViewModel

@Composable
fun CompanyScreen(viewModel: CompanyViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCompany by remember { mutableStateOf<SoftwareCompany?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var nameFilter by remember { mutableStateOf("") }
    var classificationFilter by remember { mutableStateOf("") }

    val companies by viewModel.companies.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search filters
        OutlinedTextField(
            value = nameFilter,
            onValueChange = {
                nameFilter = it
                viewModel.filterCompanies(nameFilter, classificationFilter)
            },
            label = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = classificationFilter,
            onValueChange = {
                classificationFilter = it
                viewModel.filterCompanies(nameFilter, classificationFilter)
            },
            label = { Text("Search by classification") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Company")
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(companies) { company ->
                CompanyCard(
                    company = company,
                    onEdit = { selectedCompany = company },
                    onDelete = {
                        selectedCompany = company
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || selectedCompany != null) {
        CompanyDialog(
            company = selectedCompany,
            onDismiss = {
                showAddDialog = false
                selectedCompany = null
            },
            onSave = { company ->
                if (selectedCompany == null) {
                    viewModel.addCompany(company)
                } else {
                    viewModel.updateCompany(company)
                }
                showAddDialog = false
                selectedCompany = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Company") },
            text = { Text("Are you sure you want to delete ${selectedCompany?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCompany?.id?.let { viewModel.deleteCompany(it) }
                        showDeleteDialog = false
                        selectedCompany = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}