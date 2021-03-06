package  com.example.fleet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.repositories.RegisterRepository
import com.example.fleet.repositories.ResetPasswordRepository
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap

class RegisterViewModel : BaseViewModel() {
    private var data : MutableLiveData<LoginResponse>? = null
    private var registerRepository = RegisterRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postClick = MutableLiveData<String>()

    init {
        data = registerRepository.getRegisterData(null,null)

    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return postClick
    }

    override fun clickListener(v : View) {
        postClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getRegisterResponse() : LiveData<LoginResponse> {
        return data!!
    }

    fun callRegister(hashMap : HashMap<String, RequestBody>, image: MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = registerRepository.getRegisterData(hashMap ,image)
            mIsUpdating.postValue(true)

        }
    }

    val loading : LiveData<Boolean>
        get() = mIsUpdating

}