package com.unram.asakv2.ui.ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.ArViewModel

/**
 * ArScanFragment — Scan barcode (ZXing) + render AR (ARCore).
 * Menggunakan kamera untuk scan barcode dan menampilkan konten AR.
 * [DESTI]
 */
class ArScanFragment : Fragment() {

    private lateinit var arViewModel: ArViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arViewModel = ViewModelProvider(this)[ArViewModel::class.java]

        // TODO: Setup ZXing scanner dan ARCore session
        // TODO: Observe scan result dari ViewModel
    }
}
