package com.example.myapplication

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
import com.example.myapplication.databinding.FragmentStartBarcodeInventoryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartBarcodeInventoryFragment : Fragment() {
    private var requestCamera: ActivityResultLauncher<String>? = null
    private lateinit var binding: FragmentStartBarcodeInventoryBinding
    private var namesArray: Array<String> = emptyArray()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBarcodeInventoryBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            val warehouses = getTableList("warehouses")
            val warehousesNumber = warehouses.size
            val productions = getTableList("productions")
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
                    if (p2 < warehousesNumber){
                        Config.activeInventoryTargetType = "warehouses"
                    }
                    else{
                        Config.activeInventoryTargetType = "productions"
                    }
                    Config.activeInventoryTargetName = namesArray[p2]
                    Toast.makeText(requireContext(), Config.activeInventoryTargetType, Toast.LENGTH_SHORT).show()
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
                val intent = Intent(requireContext(), BarcodeInventory::class.java)
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

    private suspend fun getTableList(table: String): Array<String> {
        var tableList = emptyArray<String>()

        withContext(Dispatchers.IO) {
            val field = arrayOfNulls<String>(1)
            field[0] = "table"
            val data = arrayOfNulls<String>(1)
            data[0] = table
            val putDataWarehouse = PutData(
                "http://192.168.0.201/Android/getTableList.php",
                "POST",
                field,
                data
            )

            if (putDataWarehouse.startPut()) {
                if (putDataWarehouse.onComplete()) {
                    val resultWarehouse = putDataWarehouse.result
                    if (resultWarehouse == "Table is empty") {
                        Toast.makeText(requireContext(), "Table is empty", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val gson = Gson()
                        val listWarehouse = gson.fromJson<List<Map<String, String>>>(
                            resultWarehouse, object : TypeToken<List<Map<String, String>>>() {}.type
                        )
                        tableList = listWarehouse.mapNotNull { it["name"] }.toTypedArray()
                    }
                }
            }
        }

        return tableList
    }


}