package com.example.user2


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
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
import java.io.IOException


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
    lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
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

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                imageUri = uri

                Glide.with(this)
                    .load(imageUri)
                    .into(imageToBeLoaded)


                // Use the uri to load the image
            }
        }

    private fun cameraCheckPermission() {
        Dexter.withContext(applicationContext)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                launcher.launch(
                                    ImagePicker.with(this@NewUser)
                                        //...
                                        .cameraOnly() // or galleryOnly()
                                        .createIntent()
                                )

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
        Dexter.withContext(applicationContext).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
//                contract.launch(arrayOf("image/*"))
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


    private val contract = registerForActivityResult(ActivityResultContracts.OpenDocument())
    {
        imageUri = it
//        imageToBeLoaded.setImageURI(it)
        Glide.with(this)
            .load(it)
            .into(imageToBeLoaded)

        Log.d("URIXYZ","$imageUri")
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

    private fun getImageUriFromBitmap(context: Context?, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)
        val path = MediaStore.Images.Media.insertImage(context!!.contentResolver,bitmap,"File",null)
        return Uri.parse(path.toString())

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