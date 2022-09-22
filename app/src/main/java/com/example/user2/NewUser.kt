package com.example.user2



import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class NewUser : AppCompatActivity() {
    val PICK_IMAGE = 1
    lateinit var myViewModel:viewModel

    val GALLERY_REQUEST_CODE = 2
    val CAMERA_REQUEST_CODE = 1

    lateinit var imageToBeLoaded: ImageView
    lateinit var saveButton: Button
    var imageUri: Uri? = null
    lateinit var editTextName: EditText
    lateinit var editTextPhone: EditText
    lateinit var editTextEmail: EditText
    var bit:Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        myViewModel= viewModel(this)
        imageToBeLoaded=findViewById(R.id.imageToBeAdded)
        saveButton = findViewById(R.id.button)
        editTextName = findViewById(R.id.etName)
        editTextEmail = findViewById(R.id.etEmail)
        editTextPhone = findViewById(R.id.etPhone)



        imageToBeLoaded.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Open Gallery","Open Camera")
            pictureDialog.setItems(pictureDialogItem) {
                    dialog, which ->
                when(which){
                    0->galleryCheckPermission()
                    1->cameraCheckPermission()
                }
            }
            pictureDialog.show()
        }
        saveButton.setOnClickListener {

            if(editTextName.text.isNotEmpty() && editTextEmail.text.isNotEmpty() && bit!=null && editTextPhone.text.isNotEmpty()){
                val userFace =
                    User(
                        0,
                        editTextName.text.toString(),
                        editTextEmail.text.toString(),
                        editTextPhone.text.toString(),
                        bit!!
                    )




                myViewModel.insertUser(userFace)

                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Snackbar.make(findViewById(R.id.constraint),"Please Enter All The Attributes",Snackbar.LENGTH_LONG).show()

            }
        }



    }

    private fun cameraCheckPermission()
    {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA).withListener(
                object : MultiplePermissionsListener
                {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()) {
                                camera()
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
    private fun galleryCheckPermission()
    {
        Dexter.withContext(this).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@NewUser,"Storage Permission Denied",Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery()
    {
        intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun camera(){
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                CAMERA_REQUEST_CODE->{
                    bit = data?.extras?.get("data") as Bitmap
                    imageToBeLoaded.setImageBitmap(bit)
                }

                GALLERY_REQUEST_CODE->{
                    data!!.data.also{ imageUri = it }
                    bit = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
                    imageToBeLoaded.setImageBitmap(bit)
                }
            }
        }
    }

    private fun showRotationalDialogForPermission(){
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    +"required for this feature. It can be enable from App Settings")
            .setPositiveButton("Go to settings"
            ) { _, _ ->
                try
                {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException)
                {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"
            ) { dialog,_ ->
                dialog.dismiss()
            }.show()
    }



    override fun onBackPressed() {
        super.onBackPressed()
        intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent)
    }
}