package com.dadm.empresa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dadm.empresa.data.DatabaseHelper
import com.dadm.empresa.data.SoftwareCompany
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {
    private val _companies = MutableStateFlow<List<SoftwareCompany>>(emptyList())
    val companies: StateFlow<List<SoftwareCompany>> = _companies.asStateFlow()

    private val _nameFilter = MutableStateFlow("")
    private val _classificationFilter = MutableStateFlow("")

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        viewModelScope.launch {
            _companies.value = dbHelper.getAllCompanies()
        }
    }

    fun addCompany(company: SoftwareCompany) {
        viewModelScope.launch {
            dbHelper.insertCompany(company)
            loadCompanies()
        }
    }

    fun updateCompany(company: SoftwareCompany) {
        viewModelScope.launch {
            dbHelper.updateCompany(company)
            loadCompanies()
        }
    }

    fun deleteCompany(id: Int) {
        viewModelScope.launch {
            dbHelper.deleteCompany(id)
            loadCompanies()
        }
    }

    fun filterCompanies(name: String, classification: String) {
        viewModelScope.launch {
            _nameFilter.value = name
            _classificationFilter.value = classification
            _companies.value = dbHelper.getFilteredCompanies(name, classification)
        }
    }
}