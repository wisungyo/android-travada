package com.example.travada.features.tabungan.formtabungansatu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.travada.R
import com.example.travada.features.tabungan.DataPermissions.Companion.CAMERA_REQUEST_UPLOAD
import com.example.travada.features.tabungan.DataPermissions.Companion.GALLERY_REQUEST_UPLOAD
import com.example.travada.features.tabungan.DataPermissions.Companion.REQUEST_CODE
import com.example.travada.features.tabungan.DataPermissions.Companion.arrayListPermission
import com.example.travada.features.tabungan.formtabungandua.FormTabunganTwoActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_form_tabungan_one.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.util.*


class FormTabunganOneActivity : AppCompatActivity(), FormTabunganOnePresenter.Listener {
    private lateinit var presenter: FormTabunganOnePresenter
    lateinit var bitmapResult: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_tabungan_one)
        presenter = FormTabunganOnePresenter(this)

        ivFormOneBack.setOnClickListener {
            finish()
        }

        // editText tujuan
        etTujuan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(count: Editable?) {
                if (count?.length!! > layoutEtTujuan.counterMaxLength) {
                    layoutEtTujuan.error = "karakter lebih dari 25"
                } else {
                    layoutEtTujuan.error = null
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                presenter.checked(
                    etTujuan.text.toString(),
                    etJumlah.text.toString(),
                    uriGambar
                )
            }
        })

        // editText jumlah
        etJumlah.addTextChangedListener(object : TextWatcher {
            var processed = ""
            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(count: Editable?) {
                val initial = count.toString()

                if (etJumlah == null) return
                if (initial.isEmpty()) return

                val cleanString = initial.replace(".", "")
                val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
                nf.setGroupingUsed(true);

                var myNumber = cleanString.toDouble()
                processed = nf.format(myNumber)
                etJumlah.removeTextChangedListener(this)
                etJumlah.setText(processed)

                try {
                    etJumlah.setSelection(processed.length)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                etJumlah.addTextChangedListener(this)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                presenter.checked(
                    etTujuan.text.toString(),
                    etJumlah.text.toString(),
                    uriGambar
                )
            }
        })

        // upload image
        ivImageTabungan.setOnClickListener {
            goToUploadGambar()
        }

        // update image
        ivEditImage.setOnClickListener {
            goToUpdateGambar()
        }
    }

    override fun btnActive() {
        btnLanjutFormOne.setBackgroundResource(R.drawable.bg_active)
        btnLanjutFormOne.setTextColor(Color.parseColor("#ffffff"))
        btnLanjutFormOne.setElevation(2f)
        btnLanjutFormOne.isClickable = true
        btnLanjutFormOne.setOnClickListener {
            val goToFormTabunganTwo = Intent(this, FormTabunganTwoActivity::class.java)
            startActivity(goToFormTabunganTwo)
        }
    }

    override fun btnInactive() {
        btnLanjutFormOne.setBackgroundResource(R.drawable.bg_inactive)
        btnLanjutFormOne.isClickable = false
    }

    override fun errTujuan(message: String?) {
        etTujuan.error = message
    }

    override fun errJumlah(message: String?) {
        etJumlah.error = message
    }

    // TODO : pengerjaan menunggu API
//    override fun goToNextPage() {
//        val intent = Intent(this, FormTabunganTwoActivity::class.java)
//        val bundle = Bundle()
//        bundle.putString("tujuan", etTujuan.text.toString())
//        bundle.putString("jumlah",etJumlah .text.toString())
//        intent.putExtras(bundle)
//        startActivity(intent)
//    }

    // ijin permissions
    fun checkPermissions(): Boolean {
        return (
                (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                        (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                )
    }

    // melakukan semua request permission yang dibutuhkan aplikasi
    fun requestRequiredPermissions() {
        requestPermissions(arrayListPermission, REQUEST_CODE)
    }

    //Callback / respon dari konfirmasi permission (pilihan allow / deny)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Cek request codenya sama seperti request code ketika meminta permission.
        when (requestCode) {
            REQUEST_CODE -> {
                //Loop semua permission apakah diizinkan atau tidak.
                for (i in permissions.indices) {
                    if ((permissions[i] == arrayListPermission[i]) && (grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(
                            this,
                            "Permission ${permissions[i]} Diizinkan",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Permission ${permissions[i]} Ditolak",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_UPLOAD) {
            if (data != null) {
                uriGambar = data.data.toString()

                // uri untuk menampilkan thumbnail
                val contentUri = data.data
                val thumbnailGalery = MediaStore.Images.Media.getBitmap(contentResolver, contentUri)
                // Toast.makeText(this, "image loaded", Toast.LENGTH_SHORT).show()
                ivImageTabungan.setImageBitmap(thumbnailGalery)
                bitmapResult = thumbnailGalery

                ivEditImage.visibility = View.VISIBLE
                img_camera.visibility = View.GONE
                ivImageTabungan.isClickable = false
                tvImageMax.visibility = View.INVISIBLE

                presenter.checked(
                    etTujuan.text.toString(),
                    etJumlah.text.toString(),
                    uriGambar
                )
            } else {
                Toast.makeText(this, "Image Loading Failed", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == CAMERA_REQUEST_UPLOAD) {
            if (data != null) {

                val thumbnailCamera = data?.extras?.get("data") as Bitmap
                ivImageTabungan.setImageBitmap(thumbnailCamera)
                bitmapResult = thumbnailCamera

                val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
                val imageFileName = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
                    .toString()
                val outputDirectory = getOutputDirectory()

                val image: File = File(
                    outputDirectory,
                    imageFileName + ".jpg"
                )

                if (!image.exists()) {
                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(image)
                        thumbnailCamera.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.flush()
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                // uri untuk disimpan
                uriGambar = Uri.fromFile(image).toString()

                ivEditImage.visibility = View.VISIBLE
                img_camera.visibility = View.GONE
                ivImageTabungan.isClickable = false
                tvImageMax.visibility = View.INVISIBLE

                presenter.checked(
                    etTujuan.text.toString(),
                    etJumlah.text.toString(),
                    uriGambar
                )
            } else {
                Toast.makeText(this, "Image Loading Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_UPLOAD)
    }

    fun takePhotoFromCamera() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intentCamera, CAMERA_REQUEST_UPLOAD)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    override fun goToUploadGambar() {
        if (checkPermissions()) {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.camera_dialog_layout, null)
            val close = view.findViewById<Button>(R.id.btnBatal)
            val kamera = view.findViewById<TextView>(R.id.tvAmbilKamera)
            val galeri = view.findViewById<TextView>(R.id.tvAmbilGaleri)
            close.setOnClickListener {
                dialog.dismiss()
            }

            kamera.setOnClickListener {
                takePhotoFromCamera()
                dialog.dismiss()
            }

            galeri.setOnClickListener {
                choosePhotoFromGallery()
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()

        } else {
            requestRequiredPermissions()
        }
    }

    override fun goToUpdateGambar() {
        if (checkPermissions()) {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.camera_dialog_layout, null)
            val close = view.findViewById<Button>(R.id.btnBatal)
            val kamera = view.findViewById<TextView>(R.id.tvAmbilKamera)
            val galeri = view.findViewById<TextView>(R.id.tvAmbilGaleri)

            close.setOnClickListener {
                dialog.dismiss()
            }

            kamera.setOnClickListener {
                takePhotoFromCamera()
                dialog.dismiss()
            }

            galeri.setOnClickListener {
                choosePhotoFromGallery()
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        } else {
            requestRequiredPermissions()
        }
    }

    companion object {
        var isError: Boolean = false
        var uriGambar: String = ""
    }
}