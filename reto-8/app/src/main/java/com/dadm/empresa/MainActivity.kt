package com.dadm.empresa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dadm.empresa.data.DatabaseHelper
import com.dadm.empresa.ui.screens.CompanyScreen
import com.dadm.empresa.ui.theme.EmpresaTheme
import com.dadm.empresa.ui.viewmodel.CompanyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DatabaseHelper(this)
        val viewModel = CompanyViewModel(dbHelper)

        setContent {
            EmpresaTheme {
                App(viewModel)
            }
        }
    }
}

@Composable
private fun App(viewModel: CompanyViewModel) {
    MaterialTheme {
        CompanyScreen(viewModel)
    }
}