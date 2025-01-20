package com.dadm.empresa.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dadm.empresa.data.SoftwareCompany

@Composable
fun CompanyCard(
    company: SoftwareCompany,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = company.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Website: ${company.website}")
            Text(text = "Phone: ${company.phone}")
            Text(text = "Email: ${company.email}")
            Text(text = "Products: ${company.products}")
            Text(text = "Classification: ${company.classification}")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}