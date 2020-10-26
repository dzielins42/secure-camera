package pl.dzielins42.seccam.ui.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PasswordViewModel : ViewModel() {

    val viewState: LiveData<PasswordViewState>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData<PasswordViewState>(PasswordViewState.Idle)

    fun validatePassword(password: String) {
        viewModelScope.launch {
            mutableViewState.postValue(PasswordViewState.Loading)
            delay(2000)

            mutableViewState.postValue(
                if ("password" == password) PasswordViewState.PasswordCorrect else PasswordViewState.PasswordIncorrect
            )
        }
    }

    fun incorrectPasswordDialogDismissed() {
        mutableViewState.value = PasswordViewState.Idle
    }
}