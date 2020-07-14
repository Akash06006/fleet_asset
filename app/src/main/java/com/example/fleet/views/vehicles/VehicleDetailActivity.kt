package com.example.fleet.views.vehicles

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.callbacks.ChoiceCallBack
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityVehicleDetailBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.vehicle.VehicleDetailResponse
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.Utils
import com.example.fleet.viewmodels.fuel.FuelViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VehicleDetailActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var vendorData: ArrayList<VendorListResponse.Data>
    lateinit var vehicleData: ArrayList<VehicleListResponse.Data>
    lateinit var addVehicleBinding: ActivityVehicleDetailBinding
    private lateinit var fuelViewModel: FuelViewModel
    private var sharedPrefClass: SharedPrefClass? = null
    private val mJsonObject = JsonObject()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var vehileImage = ""
    var vendorId = ""
    var vehicleId = ""
    var partial = 0;
    // var vehicleList = listOf<String>("Select Vehicle")
    val vehicleList = ArrayList<String>()
    val vendorList = ArrayList<String>()

    override fun onBackPressed() {
        // super.onBackPressed()
        val intent = Intent()
        setResult(3, intent)
        finish()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_vehicle_detail
    }

    override fun initViews() {
        addVehicleBinding = viewDataBinding as ActivityVehicleDetailBinding
        fuelViewModel = ViewModelProviders.of(this).get(FuelViewModel::class.java)
        addVehicleBinding.fuelViewModel = fuelViewModel
        addVehicleBinding.commonToolBar.imgRight.visibility = View.GONE
        addVehicleBinding.commonToolBar.txtEdit.visibility = View.VISIBLE
        addVehicleBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.vehicle_details)
        mJsonObject.addProperty(
            "id", "id"
        )
        makeEnableDisableViews(false)
        var id = intent.extras?.get("id").toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            fuelViewModel.getVehiclDetail(id)
        }



        fuelViewModel.getUpdateVehicleRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            showToastSuccess(getString(R.string.vehicle_update_msg))
                            // addVehicleBinding.vehicleDetailRes = response.data
                            makeEnableDisableViews(false)

                        }

                        else -> showToastError(message)
                    }

                }
            })
        fuelViewModel.getVehicleDetail().observe(this,
            Observer<VehicleDetailResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            addVehicleBinding.vehicleDetailRes = response.data
                            if (!TextUtils.isEmpty(response.data?.image)) {
                                Glide.with(this)
                                    .load(GlobalConstants.BASE_URL + "" + response.data?.image)
                                    .into(addVehicleBinding.imgVehicle)
                            }

                        }

                        else -> showToastError(message)
                    }

                }
            })

        fuelViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "etInsuranceExpiry" -> {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val datePickerDialog = DatePickerDialog(
                            this@VehicleDetailActivity,
                            DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                addVehicleBinding.etInsuranceExpiry.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                            },
                            year,
                            month,
                            day
                        )
                        datePickerDialog.getDatePicker().setMinDate(Date().getTime());
                        datePickerDialog.show()
                    }
                    "txt_edit" -> {
                        makeEnableDisableViews(true)
                    }

                    "imgEdit" -> {
                        confirmationDialog =
                            mDialogClass.setUploadConfirmationDialog(this, this, "gallery")
                    }
                    "btn_submit" -> {
                        val vehicleName = addVehicleBinding.etName.text.toString()
                        val registrationNumber =
                            addVehicleBinding.etRegistration.text.toString()
                        val model = addVehicleBinding.etVehicleModel.text.toString()
                        val color = addVehicleBinding.etVehicleColor.text.toString()
                        val engineNumber = addVehicleBinding.etEgineNumber.text.toString()
                        val expiryDate = addVehicleBinding.etInsuranceExpiry.text.toString()
                        val companyName = addVehicleBinding.etCompanyName.text.toString()
                        val insuranceNumber = addVehicleBinding.etInsuranceNumber.text.toString()

                        when {
                            /*vehileImage.isEmpty() -> {
                                showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                        R.string.invoice_image
                                    )
                                )
                            }
*/
                            else -> {
                                val mHashMap = HashMap<String, RequestBody>()

                                mHashMap["id"] =
                                    Utils(this).createPartFromString(id)
                                mHashMap["name"] =
                                    Utils(this).createPartFromString(vehicleName)
                                mHashMap["regNumber"] =
                                    Utils(this).createPartFromString(registrationNumber)
                                mHashMap["model"] =
                                    Utils(this).createPartFromString(model)
                                mHashMap["color"] =
                                    Utils(this).createPartFromString(color)
                                mHashMap["engineNumber"] =
                                    Utils(this).createPartFromString(engineNumber)
                                mHashMap["insurexpirydate"] =
                                    Utils(this).createPartFromString(expiryDate)
                                mHashMap["insurcompanyname"] =
                                    Utils(this).createPartFromString(companyName)
                                mHashMap["insurnumber"] =
                                    Utils(this).createPartFromString(insuranceNumber)

                                var userImage: MultipartBody.Part? = null
                                if (!vehileImage.isEmpty()) {
                                    val f1 = File(vehileImage)
                                    userImage = Utils(this).prepareFilePart("image", f1)
                                }

                                if (UtilsFunctions.isNetworkConnected()) {
                                    fuelViewModel.updateVehicleDetail(mHashMap, userImage)
                                    startProgressDialog()
                                }

                            }
                        }
                    }
                }
            })
        )

    }

    private fun makeEnableDisableViews(isEdit: Boolean) {
        if (isEdit) {
            addVehicleBinding.imgEdit.visibility = View.VISIBLE
            addVehicleBinding.btnSubmit.visibility = View.VISIBLE
            addVehicleBinding.commonToolBar.txtEdit.visibility = View.GONE
            addVehicleBinding.etVehicleColor.isEnabled = true
            addVehicleBinding.etInsuranceExpiry.isEnabled = true
            addVehicleBinding.etCompanyName.isEnabled = true
            addVehicleBinding.etInsuranceNumber.isEnabled = true
        } else {
            addVehicleBinding.imgEdit.visibility = View.GONE
            addVehicleBinding.btnSubmit.visibility = View.GONE
            addVehicleBinding.commonToolBar.txtEdit.visibility = View.VISIBLE
            addVehicleBinding.etVehicleColor.isEnabled = false
            addVehicleBinding.etInsuranceExpiry.isEnabled = false
            addVehicleBinding.etCompanyName.isEnabled = false
            addVehicleBinding.etInsuranceNumber.isEnabled = false
        }
    }

    override fun photoFromCamera(mKey: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(this, packageName + ".fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            vehileImage = absolutePath
        }
    }

    override fun photoFromGallery(mKey: String) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            vehileImage = picturePath
            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(vehileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_dummy_image)
            .into(addVehicleBinding.imgVehicle)
    }

    private fun showError(textView: TextView, error: String) {
        textView.requestFocus()
        textView.error = error
    }

}
