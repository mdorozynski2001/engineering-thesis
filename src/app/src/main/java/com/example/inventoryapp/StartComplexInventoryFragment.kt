package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.inventoryapp.databinding.FragmentStartComplexInventoryBinding
import kotlinx.coroutines.launch
import java.lang.StrictMath.min
import kotlin.random.Random

class StartComplexInventoryFragment : Fragment() {
    private var requestCamera: ActivityResultLauncher<String>? = null
    private lateinit var binding: FragmentStartComplexInventoryBinding
    private var namesArray: Array<String> = emptyArray()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartComplexInventoryBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            val warehouses = getTableNamesList("warehouses")
            val warehousesNumber = warehouses.size
            val productions = getTableNamesList("productions")
            namesArray += warehouses
            namesArray += productions

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                namesArray
            )
            binding.spinner.adapter = arrayAdapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    if (p2 < warehousesNumber) {
                        ActiveInventory.type = "warehouses"
                    } else {
                        ActiveInventory.type = "productions"
                    }
                    ActiveInventory.name = namesArray[p2]
                    Toast.makeText(
                        requireContext(),
                        ActiveInventory.type,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

        binding.radioGroup1.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.radioButtonAllProducts -> {
                    binding.button.isEnabled = true
                    binding.randomProductsNumber.isEnabled = false
                    ActiveInventory.allOrRandomProducts = "AllProducts"
                }
                R.id.radioButtonRandomProducts -> {
                    binding.button.isEnabled = true
                    binding.randomProductsNumber.isEnabled = true
                    ActiveInventory.allOrRandomProducts = "RandomProducts"
                }
            }
        }

        requestCamera = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) {
            if (it) {
                val intent = Intent(requireContext(), ComplexInventory::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Permission not Granted", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        binding.button.setOnClickListener {
            if (getInventoryId()) {
                if (getProductsFromInventoryTarget()) {
                    when (ActiveInventory.allOrRandomProducts) {
                        "AllProducts" -> {
                            ActiveInventory.productsNumberToInventory = ActiveInventory.productsNumberTotal
                            requestCamera?.launch(android.Manifest.permission.CAMERA)
                        }
                        "RandomProducts" -> {
                            val number = binding.randomProductsNumber.text.toString()
                            if (number.isNotEmpty()) {
                                if (number.toInt() > 0) {
                                    ActiveInventory.productsNumberToInventory =
                                        min(number.toInt(), ActiveInventory.productsNumberTotal)
                                    val randomNumbers = mutableSetOf<Int>()

                                    while (randomNumbers.size < ActiveInventory.productsNumberToInventory) {
                                        val randomNumber = Random.nextInt(
                                            0,
                                            ActiveInventory.productsNumberTotal
                                        )
                                        randomNumbers.add(randomNumber)
                                    }
                                    ActiveInventory.productsIDsInList = randomNumbers.toList()
                                    Log.i(
                                        "PutData",
                                        "Chosen products ids: ${ActiveInventory.productsIDsInList}"
                                    )
                                    requestCamera?.launch(android.Manifest.permission.CAMERA)
                                }
                            }
                        }
                    }
                } else {
                    Log.i("PutData", "Calculating total product number failed")
                }
            } else {
                Log.i("PutData", "ID not found")
            }
        }

        return binding.root
    }


}