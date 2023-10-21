package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentStartBarcodeInventoryBinding

class StartBarcodeInventoryFragment : Fragment() {
    private var requestCamera : ActivityResultLauncher<String>? = null
    private lateinit var binding: FragmentStartBarcodeInventoryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBarcodeInventoryBinding.inflate(inflater, container, false)
        requestCamera = registerForActivityResult(ActivityResultContracts
            .RequestPermission()){
                if(it){
                    val intent = Intent(requireContext(), BarcodeInventory::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(requireContext(),"Permission not Granted",Toast.LENGTH_SHORT).show()

                }
        }
        binding.button.setOnClickListener {
            requestCamera?.launch(android.Manifest.permission.CAMERA)
        }

        return binding.root
    }
}