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
    private var warehouses: Array<String> = emptyArray()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartComplexInventoryBinding.inflate(inflater, container, false)

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
                    binding.randomProductsAmount.isEnabled = false
                    ActiveInventory.allOrRandomProducts = "AllProducts"
                }
                R.id.radioButtonRandomProducts -> {
                    binding.button.isEnabled = true
                    binding.randomProductsAmount.isEnabled = true
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
                            ActiveInventory.productsAmountToInventory = ActiveInventory.productsAmountTotal
                            requestCamera?.launch(android.Manifest.permission.CAMERA)
                        }
                        "RandomProducts" -> {
                            val amount = binding.randomProductsAmount.text.toString()
                            if (amount.isNotEmpty()) {
                                if (amount.toInt() > 0) {
                                    ActiveInventory.productsAmountToInventory =
                                        min(amount.toInt(), ActiveInventory.productsAmountTotal)
                                    val randomAmounts = mutableSetOf<Int>()

                                    while (randomAmounts.size < ActiveInventory.productsAmountToInventory) {
                                        val randomAmount = Random.nextInt(
                                            0,
                                            ActiveInventory.productsAmountTotal
                                        )
                                        randomAmounts.add(randomAmount)
                                    }
                                    ActiveInventory.productsIDsInList = randomAmounts.toList()
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
                    Log.i("PutData", "Calculating total product amount failed")
                }
            } else {
                Log.i("PutData", "ID not found")
            }
        }

        return binding.root
    }


}