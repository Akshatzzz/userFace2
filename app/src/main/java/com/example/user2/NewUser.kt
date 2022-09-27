package com.example.user2


import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLU
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.Media.insertImage
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class NewUser : AppCompatActivity() {
    val PICK_IMAGE = 1
    lateinit var myViewModel: viewModel

    val GALLERY_REQUEST_CODE = 2
    val CAMERA_REQUEST_CODE = 1

    lateinit var imageToBeLoaded: ImageView
    lateinit var saveButton: Button
    var imageUri: Uri? = null
    lateinit var bitmap: Bitmap
    lateinit var editTextName: EditText
    lateinit var editTextPhone: EditText
    lateinit var editTextEmail: EditText
    var bit: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        myViewModel = viewModel(this)
        imageToBeLoaded = findViewById(R.id.imageToBeAdded)
        saveButton = findViewById(R.id.button)
        editTextName = findViewById(R.id.etName)
        editTextEmail = findViewById(R.id.etEmail)
        editTextPhone = findViewById(R.id.etPhone)



        imageToBeLoaded.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Open Gallery", "Open Camera")
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                when (which) {
                    0 -> galleryCheckPermission()
                    1 -> cameraCheckPermission()
                }
            }
            pictureDialog.show()
        }
        saveButton.setOnClickListener {

            if (editTextName.text.isNotEmpty() && editTextEmail.text.isNotEmpty() && imageUri != null && editTextPhone.text.isNotEmpty()) {
                val userFace =
                    User(
                        0,
                        editTextName.text.toString(),
                        editTextEmail.text.toString(),
                        editTextPhone.text.toString(),
                        imageUri.toString()
                    )




                myViewModel.insertUser(userFace)

                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Snackbar.make(
                    findViewById(R.id.constraint),
                    "Please Enter All The Attributes",
                    Snackbar.LENGTH_LONG
                ).show()

            }
        }


    }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        Glide.with(this)
            .load(imageUri)
            .into(imageToBeLoaded)
    }

    private fun createImageUri(): Uri? {
        val time = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())
        val image = File(/* parent = */ applicationContext.filesDir, /* child = */
            "camera_photo_${time}.png"
        )

        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.user2.fileProvider",
            image
        )
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    private fun cameraCheckPermission() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
//                                camera()
                                imageUri = createImageUri()
                                contract.launch(imageUri)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }
                }
            ).onSameThread().check()
    }

    private fun galleryCheckPermission() {
        Dexter.withContext(this).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@NewUser, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun camera() {
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
//                CAMERA_REQUEST_CODE -> {
//                    bit = data?.extras?.get("data") as Bitmap
//                    imageToBeLoaded.setImageBitmap(bit)
//
//                    val tempUri: Uri? = getImageUriFromBitmap(applicationContext, bit!!)
////                    val finalFile:File = File(getRealPathFromURI(tempUri))
//                    imageUri = tempUri
//                    Glide.with(this)
//                        .load(imageUri)
//                        .into(imageToBeLoaded)
//                }

                GALLERY_REQUEST_CODE -> {
                    data!!.data.also { imageUri = it }
                    bit = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    val tempUri: Uri = getImageUriFromBitmap(applicationContext, bit!!)
//                    val finalFile:File = File(getRealPathFromURI(tempUri))
                    imageUri = tempUri

                    Glide.with(this)
                        .load(imageUri)
                        .into(imageToBeLoaded)
//                    imageToBeLoaded.setImageBitmap(bit)

                }
            }
        }
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(
                "It looks like you have turned off permissions"
                        + "required for this feature. It can be enable from App Settings"
            )
            .setPositiveButton(
                "Go to settings"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}