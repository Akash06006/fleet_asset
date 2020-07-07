package com.example.fleet.views.services

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.callbacks.ChoiceCallBack
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityUpdateServiceBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.vendor.VendorListResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.Utils
import com.example.fleet.viewmodels.services.ServicesViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateServiceActivity : BaseActivity(), ChoiceCallBack {
    val vendorList = ArrayList<String>()
    private lateinit var vendorData: ArrayList<VendorListResponse.Data>
    lateinit var updateServiceBinding: ActivityUpdateServiceBinding
    lateinit var servicesViewModel: ServicesViewModel
    private var mJsonObject = JSONObject()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    var final = ""
    var vendorId: String? = null

    override fun initViews() {
        updateServiceBinding = viewDataBinding as ActivityUpdateServiceBinding
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        updateServiceBinding.serviceViewModel = servicesViewModel
        updateServiceBinding.commonToolBar.imgToolbarText.text =
                resources.getString(R.string.update_service_entry)

        try {
            mJsonObject = JSONObject(intent.extras.get("data").toString())
            vendorId = mJsonObject.get("vendor_id").toString()
            var vendorName = mJsonObject.get("vendor_name").toString()
            if (!TextUtils.isEmpty(vendorName) && vendorName != "null") {
                updateServiceBinding.etVendorName.visibility = View.VISIBLE
                updateServiceBinding.spVendor.visibility = View.GONE
                updateServiceBinding.etVendorName.setText(vendorName)

            } else {
                updateServiceBinding.etVendorName.visibility = View.GONE
                updateServiceBinding.spVendor.visibility = View.VISIBLE
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        servicesViewModel.updateServiceStatus().observe(this,
                Observer<CommonModel> { response ->
                    stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                finish()
                                showToastSuccess(message)
                            }
                            else -> showToastError(message)
                        }

                    }
                })

        servicesViewModel.getVendorList().observe(this,
                Observer<VendorListResponse> { response ->
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
                                setVendorSpinner()
                            }
                            /* response.code == 204 -> {
                                 FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                             }*/
                            else -> showToastError(message)
                        }

                    }
                })

        servicesViewModel.isClick().observe(
                this, Observer<String>(function =
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun(it: String?) {
            when (it) {
                "et_date_service" -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val datePickerDialog = DatePickerDialog(
                            this@UpdateServiceActivity,
                            DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                updateServiceBinding.etDateService.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
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
                    val invoiceNumber = updateServiceBinding.etInvoice.text.toString()
                    val date = updateServiceBinding.etDateService.text.toString()
                    val odometer = updateServiceBinding.etOdometer.text.toString()
                    val laborPrice = updateServiceBinding.etLabourPrice.text.toString()
                    val partsPrice = updateServiceBinding.etPartsPrice.text.toString()
                    val tax = updateServiceBinding.etTax.text.toString()
                    val totalPrice = updateServiceBinding.etTotalPrice.text.toString()
                    val additionalInfo = updateServiceBinding.etComments.text.toString()

                    when {
                        profileImage.isEmpty() -> {
                            showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                            R.string.invoice_image
                                    )
                            )
                        }
                        invoiceNumber.isEmpty() -> showError(
                                updateServiceBinding.etInvoice,
                                getString(R.string.empty) + " " + getString(
                                        R.string.invoice_number
                                )
                        )
                        date.isEmpty() -> {
                            showToastError(
                                    getString(R.string.please_select) + " " + getString(
                                            R.string.select_date
                                    )
                            )
                        }
                        odometer.isEmpty() -> showError(
                                updateServiceBinding.etOdometer,
                                getString(R.string.empty) + " " + getString(
                                        R.string.odometer
                                )
                        )
                        laborPrice.isEmpty() -> showError(
                                updateServiceBinding.etLabourPrice,
                                getString(R.string.empty) + " " + getString(
                                        R.string.labor_price
                                )
                        )
                        partsPrice.isEmpty() -> showError(
                                updateServiceBinding.etPartsPrice,
                                getString(R.string.empty) + " " + getString(
                                        R.string.parts_price
                                )
                        )
                        tax.isEmpty() -> showError(
                                updateServiceBinding.etTax,
                                getString(R.string.empty) + " " + getString(
                                        R.string.tax
                                )
                        )
                        totalPrice.isEmpty() -> showError(
                                updateServiceBinding.etTotalPrice,
                                getString(R.string.empty) + " " + getString(
                                        R.string.total_price
                                )
                        )
                        additionalInfo.isEmpty() -> showError(
                                updateServiceBinding.etComments,
                                getString(R.string.empty) + " " + getString(
                                        R.string.additional_information
                                )
                        )
                        else -> {
                            val androidId = UtilsFunctions.getAndroidID()
                            val mHashMap = HashMap<String, RequestBody>()
//vehicleId
                            //serviceFor
                            //serviceFor
                            //renewalType

                            mHashMap["vehicleId"] =
                                    Utils(this).createPartFromString(mJsonObject.get("vehicle_id").toString())
                            mHashMap["updateFrom"] =
                                    Utils(this).createPartFromString("mobile")
                            mHashMap["serviceId"] =
                                    Utils(this).createPartFromString(mJsonObject.get("service_id").toString())
                            mHashMap["serviceDate"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etDateService.text.toString())
                            mHashMap["vendorId"] =
                                    Utils(this).createPartFromString(vendorId.toString())
                            mHashMap["laborPrice"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etLabourPrice.text.toString())
                            mHashMap["odometer"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etOdometer.text.toString())
                            mHashMap["partsPrice"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etPartsPrice.text.toString())
                            mHashMap["tax"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etTax.text.toString())
                            mHashMap["totalPrice"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etTotalPrice.text.toString())
                            mHashMap["invoiceNumber"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etInvoice.text.toString())
                            mHashMap["comments"] =
                                    Utils(this).createPartFromString(updateServiceBinding.etComments.text.toString())
                            var userImage: MultipartBody.Part? = null
                            if (!profileImage.isEmpty()) {
                                val f1 = File(profileImage)
                                userImage = Utils(this).prepareFilePart("invoiceImage", f1)
                            }
                            if (UtilsFunctions.isNetworkConnected()) {
                                servicesViewModel.updateService(mHashMap, userImage)
                                startProgressDialog()
                            }

                        }
                    }
                }
            }

        })
        )

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_update_service
    }

    private fun setVendorSpinner() {
        val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, vendorList
        )
        updateServiceBinding.spVendor.adapter = adapter

        updateServiceBinding.spVendor.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
            ) {
                if (position > 0)
                    vendorId = vendorData[position - 1].vendor_id.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
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
            profileImage = absolutePath
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
            profileImage = picturePath
            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(profileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setImage(path: String) {
        Glide.with(this)
                .load(path)
                .placeholder(R.drawable.ic_dummy_image)
                .into(updateServiceBinding.imgProfile)
    }

    private fun showError(textView: TextView, error: String) {
        textView.requestFocus()
        textView.error = error
    }

}
