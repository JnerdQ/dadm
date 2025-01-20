package com.dadm.empresa.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dadm.empresa.data.SoftwareCompany
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@Composable
fun CompanyDialog(
    company: SoftwareCompany?,
    onDismiss: () -> Unit,
    onSave: (SoftwareCompany) -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var website by remember { mutableStateOf(company?.website ?: "") }
    var phone by remember { mutableStateOf(company?.phone ?: "") }
    var email by remember { mutableStateOf(company?.email ?: "") }
    var products by remember { mutableStateOf(company?.products ?: "") }
    var classification by remember { mutableStateOf(company?.classification ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (company == null) "Add Company" else "Edit Company") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = products,
                    onValueChange = { products = it },
                    label = { Text("Products and Services") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = classification,
                    onValueChange = { classification = it },
                    label = { Text("Classification") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        SoftwareCompany(
                            id = company?.id ?: 0,
                            name = name,
                            website = website,
                            phone = phone,
                            email = email,
                            products = products,
                            classification = classification
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}