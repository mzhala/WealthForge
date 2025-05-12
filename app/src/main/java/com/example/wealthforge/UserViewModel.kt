import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    fun setUserId(id: String) {
        _userId.value = id
    }
}
