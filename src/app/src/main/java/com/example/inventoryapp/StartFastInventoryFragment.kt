package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.inventoryapp.databinding.FragmentStartFastInventoryBinding
import kotlinx.coroutines.launch

class StartFastInventoryFragment : Fragment() {
    private var requestCamera: ActivityResultLauncher<String>? = null
    private lateinit var binding: FragmentStartFastInventoryBinding
    private var warehouses: Array<String> = emptyArray()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartFastInventoryBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            warehouses += getTableNamesList("stock")

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                warehouses
            )
            binding.spinner.adapter = arrayAdapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    ActiveInventory.name = warehouses[p2]
                    binding.button.isEnabled = true
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

        requestCamera = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) {
            if (it) {
                getInventoryId()
                val intent = Intent(requireContext(), FastInventory::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Permission not Granted", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        binding.button.setOnClickListener {
            requestCamera?.launch(android.Manifest.permission.CAMERA)
        }

        return binding.root
    }
}