package com.example.fleet.views.fuel

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.callbacks.ChoiceCallBack
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityAddFuelDetailBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.Utils
import com.example.fleet.viewmodels.fuel.FuelViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_fuel_detail.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddFuelDetailActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var vendorData : ArrayList<VendorListResponse.Data>
    lateinit var vehicleData : ArrayList<VehicleListResponse.Data>
    lateinit var addFuelDetailBinding : ActivityAddFuelDetailBinding
    private lateinit var fuelViewModel : FuelViewModel
    private var sharedPrefClass : SharedPrefClass? = null
    private val mJsonObject = JsonObject()
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    var vendorId =""
    var vehicleId : Int? = null
    var partial = 0;
    // var vehicleList = listOf<String>("Select Vehicle")
    val vehicleList = ArrayList<String>()
    val vendorList = ArrayList<String>()

    override fun getLayoutId() : Int {
        return R.layout.activity_add_fuel_detail
    }

    override fun initViews() {
        addFuelDetailBinding = viewDataBinding as ActivityAddFuelDetailBinding
        fuelViewModel = ViewModelProviders.of(this).get(FuelViewModel::class.java)
        addFuelDetailBinding.fuelViewModel = fuelViewModel
        addFuelDetailBinding.commonToolBar.imgRight.visibility = View.GONE
        addFuelDetailBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.fuel_entry)
        mJsonObject.addProperty(
            "id", "id"
        )

        if (UtilsFunctions.isNetworkConnected()) {
            fuelViewModel.getVendorList()
            fuelViewModel.getVehicleList()
        }


        fuelViewModel.getVehicleList().observe(this,
            Observer<VehicleListResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleData = response.data!!
                            vehicleList.add(getString(R.string.select_vehicle))
                            for (i in response.data!!) {
                                vehicleList.add(i.vehicle_name!!)
                            }
                            setVehicleSpinner()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })

        fuelViewModel.getFuelResponse().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            showToastSuccess(message)
                            finish()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })
        fuelViewModel.getVendorList().observe(this,
            Observer<VendorListResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vendorData = response.data!!
                            vendorList.add(getString(R.string.select_vendor))
                            for (i in response.data!!) {
                                vendorList.add(i.vendor_name!!)
                            }
                            setVendorSpinner();
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })
        fuelViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "et_date" -> {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val datePickerDialog = DatePickerDialog(
                            this@AddFuelDetailActivity,
                            DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth->
                                addFuelDetailBinding.etDate.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year)
                            },
                            year,
                            month,
                            day
                        )
                        datePickerDialog.getDatePicker().setMaxDate(Date().getTime());
                        datePickerDialog.show()
                    }
                    "img_profile" -> {
                        confirmationDialog =
                            mDialogClass.setUploadConfirmationDialog(this, this, "gallery")
                    }
                    "btn_submit" -> {
                        val invoiceNumber = addFuelDetailBinding.etInvoice.text.toString()
                        val vehicleName = addFuelDetailBinding.spVehicle.selectedItem.toString()
                        val date = addFuelDetailBinding.etDate.text.toString()
                        val odometer = addFuelDetailBinding.etOdometer.text.toString()
                        val price = addFuelDetailBinding.etPrice.text.toString()
                        val vendor = addFuelDetailBinding.spVendor.selectedItem.toString()

                        when {
                            profileImage.isEmpty() -> {
                                showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                        R.string.invoice_image
                                    )
                                )
                            }
                            invoiceNumber.isEmpty() -> showError(
                                addFuelDetailBinding.etInvoice,
                                getString(R.string.empty) + " " + getString(
                                    R.string.invoice_number
                                )
                            )
                            vehicleName == getString(R.string.select_vehicle) -> {
                                showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                        R.string.select_vehicle
                                    )
                                )
                            }
                            date.isEmpty() -> {
                                showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                        R.string.select_date
                                    )
                                )
                            }
                            odometer.isEmpty() -> showError(
                                addFuelDetailBinding.etOdometer,
                                getString(R.string.empty) + " " + getString(
                                    R.string.odometer
                                )
                            )
                            price.isEmpty() -> showError(
                                addFuelDetailBinding.etPrice,
                                getString(R.string.empty) + " " + getString(
                                    R.string.price
                                )
                            )
                            vendor == getString(R.string.select_vendor) -> {
                                showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                        R.string.select_vendor
                                    )
                                )
                            }
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
                                /*vehicle_id:1
entry_date:2020-03-01 23:00:00
odometer:12
partial_fuelup:1
price:15
vendor_id:1
invoice_number:
invoice_image :*/
                                mHashMap["date"] =
                                    Utils(this).createPartFromString(addFuelDetailBinding.etDate.text.toString())
                                mHashMap["odometer"] =
                                    Utils(this).createPartFromString(addFuelDetailBinding.etOdometer.text.toString())
                                mHashMap["partialFuelup"] =
                                    Utils(this).createPartFromString(partial.toString())
                                mHashMap["price"] =
                                    Utils(this).createPartFromString(addFuelDetailBinding.etPrice.text.toString())
                                mHashMap["vendorId"] =
                                    Utils(this).createPartFromString(vendorId)
                                mHashMap["invoiceNumber"] =
                                    Utils(this).createPartFromString(addFuelDetailBinding.etInvoice.text.toString())
                                mHashMap["vehicleId"] =
                                    Utils(this).createPartFromString(vehicleId.toString())
                                var userImage : MultipartBody.Part? = null
                                if (!profileImage.isEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage = Utils(this).prepareFilePart("image", f1)
                                }

                                if (UtilsFunctions.isNetworkConnected()) {
                                    fuelViewModel.addFuelEntry(mHashMap, userImage)
                                    startProgressDialog()
                                }

                            }
                        }
                    }
                }
            })
        )

        addFuelDetailBinding.radioGroupPartial.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId->
                val radio : RadioButton = findViewById(checkedId)
                if (radio == radioNo) {
                    partial = 0
                } else {
                    partial = 1
                }
                /* Toast.makeText(
                     applicationContext, " On checked change :" +
                             " ${radio.text}",
                     Toast.LENGTH_SHORT
                 ).show()*/
            })
    }

    private fun setVendorSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, vendorList
        )
        addFuelDetailBinding.spVendor.adapter = adapter

        addFuelDetailBinding.spVendor.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                if (position > 0)
                    vendorId = vendorData[position - 1].vendor_id!!
            }

            override fun onNothingSelected(parent : AdapterView<*>) {

            }
        }
    }

    private fun setVehicleSpinner() {
        val vehicleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, vehicleList
        )
        addFuelDetailBinding.spVehicle.adapter = vehicleAdapter

        addFuelDetailBinding.spVehicle.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                if (position > 0)
                    vehicleId = vehicleData[position - 1].vehicle_id
            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }

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
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
            .placeholder(R.drawable.ic_dummy_image)
            .into(addFuelDetailBinding.imgProfile)
    }

    private fun showError(textView : TextView, error : String) {
        textView.requestFocus()
        textView.error = error
    }

}
