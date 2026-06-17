package com.unram.asakv2.ui.ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * ArBudayaUnlockFragment — Konfirmasi unlock AR budaya setelah scan.
 * Menampilkan dialog konfirmasi setelah barcode berhasil di-scan.
 * [DESTI]
 */
class ArBudayaUnlockFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar_budaya_unlock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Tampilkan info konten AR budaya yang di-unlock
        // TODO: Setup tombol konfirmasi unlock
    }
}
