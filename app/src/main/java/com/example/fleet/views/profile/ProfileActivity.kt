package com.example.fleet.views.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.callbacks.ChoiceCallBack
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityProfileBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.*
import com.example.fleet.viewmodels.profile.ProfileViewModel
import com.example.fleet.views.authentication.OTPVerificationActivity
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var profileBinding : ActivityProfileBinding
    private lateinit var profieViewModel : ProfileViewModel
    private var sharedPrefClass : SharedPrefClass? = null
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_profile
    }

    override fun initViews() {
        profileBinding = viewDataBinding as ActivityProfileBinding
        profieViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileBinding.profileViewModel = profieViewModel
        profileBinding.commonToolBar.imgRight.visibility = View.VISIBLE
        profileBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_nav_edit_icon)
        profileBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.view_profile)

        makeEnableDisableViews(false)
        mJsonObject.addProperty(
            "id", "id"/* sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()*/
        )
        val id= SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            profieViewModel.getProfileDetail(id)
        }

        profieViewModel.getDetail().observe(this,
            Observer<LoginResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            profileBinding.profileModel = response.data
                            /* val intent = Intent(this, OTPVerificationActivity::class.java)
                             intent.putExtra("data", mJsonObject.toString())
                             startActivity(intent)*/

                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })

        profieViewModel.getUpdateDetail().observe(this,
            Observer<LoginResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            profileBinding.profileModel = response.data
                            showToastSuccess(message)
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USER_IAMGE,
                                response.data!!.profileImageUrl
                            )
                            SharedPrefClass().putObject(
                                applicationContext,
                                getString(R.string.first_name),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
                            makeEnableDisableViews(false)
                        }
                        else -> showToastError(message)
                    }

                }
            })


        profieViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "iv_edit" -> {
                        // editImage = 0
                        //  showPictureDialog()
                    }
                    "img_right" -> {
                        // isEditable = true
                        profileBinding.commonToolBar.imgToolbarText.text =
                            resources.getString(R.string.edit_profile)
                        makeEnableDisableViews(true)
                    }
                    "upload_profile_layer" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(this, this, "gallery")
                        }

                    }
                    "btn_submit" -> {
                        val fname = profileBinding.etFirstname.text.toString()
                        val lname = profileBinding.etLastname.text.toString()
                        val email = profileBinding.etEmail.text.toString()
                        val address = profileBinding.etAddress.text.toString()

                        when {
                            fname.isEmpty() -> showError(
                                profileBinding.etFirstname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lname.isEmpty() -> showError(
                                profileBinding.etLastname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                profileBinding.etEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    profileBinding.etEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            else -> {
                                val phonenumber = SharedPrefClass().getPrefValue(
                                    MyApplication.instance,
                                    getString(R.string.key_phone)
                                ) as String
                                val countrycode = SharedPrefClass().getPrefValue(
                                    MyApplication.instance,
                                    getString(R.string.key_country_code)
                                ) as String
                                val androidId = UtilsFunctions.getAndroidID()
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["fName"] = Utils(this).createPartFromString(fname)
                                mHashMap["lName"] = Utils(this).createPartFromString(lname)
                                mHashMap["updateFrom"] = Utils(this).createPartFromString("mobile")
                               /* mHashMap["phone_number"] =
                                    Utils(this).createPartFromString(phonenumber)
                                mHashMap["country_code"] =
                                    Utils(this).createPartFromString(countrycode)
                                mHashMap["device_id"] = Utils(this).createPartFromString(androidId)*/
                                mHashMap["employeeId"] =
                                    Utils(this).createPartFromString( SharedPrefClass().getPrefValue(
                                        MyApplication.instance,
                                        GlobalConstants.USERID
                                    ) as String)
                                mHashMap["address"] = Utils(this).createPartFromString(address)
                               // mHashMap["gender"] = Utils(this).createPartFromString("1")
                               /* mHashMap["notify_id"] = Utils(this).createPartFromString(
                                    SharedPrefClass().getPrefValue(
                                        MyApplication.instance,
                                        GlobalConstants.NOTIFICATION_TOKEN
                                    ) as String
                                )*/
                                mHashMap["email"] = Utils(this).createPartFromString(email)
                                //  mHashMap["password"] = Utils(this).createPartFromString(password)
                                var userImage : MultipartBody.Part? = null
                                if (!profileImage.isEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage = Utils(this).prepareFilePart("image", f1)
                                }
                                if (UtilsFunctions.isNetworkConnected()) {
                                    startProgressDialog()
                                    profieViewModel.updateProfile(mHashMap, userImage)
                                }

                            }
                        }

                    }
                }
            })
        )

    }

    private fun makeEnableDisableViews(isEnable : Boolean) {
        profileBinding.etFirstname.isEnabled = isEnable
        profileBinding.etLastname.isEnabled = isEnable
        profileBinding.etEmail.isEnabled = isEnable
        profileBinding.etPhone.isEnabled = false
        profileBinding.etAddress.isEnabled = isEnable
        if (!isEnable) {
            profileBinding.ivEdit.visibility = View.GONE
            profileBinding.btnSubmit.visibility = View.GONE
            profileBinding.commonToolBar.imgRight.visibility = View.VISIBLE
        } else {
            profileBinding.ivEdit.visibility = View.VISIBLE
            profileBinding.commonToolBar.imgRight.visibility = View.GONE
            profileBinding.btnSubmit.visibility = View.VISIBLE
        }

    }

    private fun showError(textView : TextView, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun photoFromCamera(mKey : String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile : File? = try {
                    createImageFile()
                } catch (ex : IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI : Uri =
                        FileProvider.getUriForFile(this, packageName + ".fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile() : File {
        // Create an image file name
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            profileImage = absolutePath
        }
    }

    override fun photoFromGallery(mKey : String) {
        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            profileImage = picturePath
            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(profileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setImage(path : String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.user)
            .into(profileBinding.imgProfile)
    }
}
