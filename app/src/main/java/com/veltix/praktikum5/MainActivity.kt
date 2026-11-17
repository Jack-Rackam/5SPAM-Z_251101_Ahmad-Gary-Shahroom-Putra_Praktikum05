package com.veltix.praktikum5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView

class MainActivity : LoggedActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MahasiswaAdapter
    private lateinit var btnUrutan: Button
    private val dataMahasiswa = mutableListOf<Mahasiswa>()

    // State layar: urut berdasarkan NIM (true) atau nama (false).
    private var urutNim = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        adapter = MahasiswaAdapter(this, dataMahasiswa)
        btnUrutan = findViewById(R.id.btnUrutan)
        val listView = findViewById<ListView>(R.id.listMahasiswa)

        listView.adapter = adapter

        findViewById<Button>(R.id.btnTambah).setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        btnUrutan.setOnClickListener {
            urutNim = !urutNim
            loadData()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(
                DetailActivity.EXTRA_ID,
                dataMahasiswa[position].id
            )
            startActivity(intent)
        }

        if (savedInstanceState != null) {
            Log.d("Data", "onCreate state=dipulihkan")
        } else {
            Log.d("Data", "onCreate state=baru")
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        dataMahasiswa.clear()
        dataMahasiswa.addAll(db.getAllMahasiswa(urutNim))
        adapter.notifyDataSetChanged()

        btnUrutan.text =
            if (urutNim) "Urut: NIM"
            else "Urut: Nama"

        Log.i(
            "Data",
            "Memuat ${dataMahasiswa.size} baris, urutNim=$urutNim"
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_URUT_NIM, urutNim)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        urutNim = savedInstanceState.getBoolean(
            KEY_URUT_NIM,
            false
        )
    }

    companion object {
        private const val KEY_URUT_NIM = "urut_nim"
    }
}