package com.example.travada.features.tabungan.formresulttabungan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travada.R
import com.example.travada.features.tabungan.adapter.BarengTemanAdapter
import com.example.travada.features.tabungan.adapter.TabungBarengAdapter
import com.example.travada.features.tabungan.formtabungandua.FormTabunganTwoPresenter
import com.example.travada.features.tabungan.maintabungan.TabunganActivity
import com.example.travada.features.tabungan.models.DataTabungBareng
import kotlinx.android.synthetic.main.activity_detail_form_result.*
import kotlinx.android.synthetic.main.activity_form_tabungan_two.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DetailFormResultActivity : AppCompatActivity(), DetailFormResultPresenter.Listener {
    private lateinit var presenter: DetailFormResultPresenter
    private lateinit var bundle: Bundle

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_form_result)

        presenter = DetailFormResultPresenter(this)
        intent?.extras?.let { bundle = it }
        btnBuatSekarang.setOnClickListener {
            val gotoMainTabungan = Intent(this, TabunganActivity::class.java)
            startActivity(gotoMainTabungan)
        }

        ivFormResultBack.setOnClickListener {
            finish()
        }

//        intent.putExtra("uriGambar", uriGambar)
//        intent.putExtra("jumlahDitabung", jumlahDitabung)
//        intent.putExtra("namaTujuan", tujuan)
//        intent.putExtra("tanggalTarget", etTanggal.text.toString())
//        intent.putExtra("setoranAwal", etSetoranAwal.text.toString())
//        intent.putExtra("metodeTabungan", etMetodeTabungan.text.toString())
//        intent.putExtra("periodeTabungan", etPeriodeTabungan.text.toString())
//        intent.putExtra("jumlahSetoran", etJumlahSetoran.text.toString())

//        val namaTujuan = bundle.getString("namaTujuan")
        val namaTujuan = intent.getStringExtra("namaTujuan")
        tvNamaTujuan.setText(namaTujuan)

//        val jumlahDitabung = bundle.getString("jumlahDitabung")
        val jumlahDitabung = intent.getStringExtra("jumlahDitabung")
        tvJumlahDitabung.text = "Rp. ${jumlahDitabung}"

//        val setoranAwal = bundle.getString("setoranAwal")
        val setoranAwal = intent.getStringExtra("setoranAwal")
        tvSetoranAwal.text = "Rp. ${setoranAwal}"

//        val tabunganBulanan = bundle.getString("jumlahSetoran")
        val tabunganBulanan = intent.getStringExtra("jumlahSetoran")
        tvTabunganBulanan.text = "Rp. ${tabunganBulanan}"

//        val tanggalTarget = bundle.getString("tanggalTarget")
        val tanggalTarget = intent.getStringExtra("tanggalTarget")
        tvTanggalTarget.setText(tanggalTarget)
    }

    override fun showLoadingDialog() {
        TODO("Not yet implemented")
    }

    override fun hideLoadingDialog() {
        TODO("Not yet implemented")
    }

    override fun showToast(text: String) {
        TODO("Not yet implemented")
    }

    override fun showDataTabungBareng(adapterTabungBareng: BarengTemanAdapter) {
        val layoutManagerLinear =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvDetail.layoutManager = layoutManagerLinear
        rvDetail.adapter = adapterTabungBareng
        rvDetail.overScrollMode = View.OVER_SCROLL_NEVER
    }
}