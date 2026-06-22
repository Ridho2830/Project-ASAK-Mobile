package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.ArBudaya

class ArViewModel : ViewModel() {

    private val _scanResult = MutableLiveData<String?>()
    val scanResult: LiveData<String?> = _scanResult

    private val _arBudaya = MutableLiveData<ArBudaya?>()
    val arBudaya: LiveData<ArBudaya?> = _arBudaya

    private val _modelUrl = MutableLiveData<String?>()
    val modelUrl: LiveData<String?> = _modelUrl

    private val _isUnlocked = MutableLiveData<Boolean>()
    val isUnlocked: LiveData<Boolean> = _isUnlocked

    fun onBarcodeScanned(barcode: String) {
        _scanResult.value = barcode
        
    }

    fun unlockArBudaya(userId: String, arBudayaId: String) {
        
    }

    fun loadModel3D(modelUrl: String) {
        
    }
}
