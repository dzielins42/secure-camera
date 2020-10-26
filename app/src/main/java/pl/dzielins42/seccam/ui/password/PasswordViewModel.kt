package pl.dzielins42.seccam.ui.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.dzielins42.seccam.data.repo.PasswordRepository

class PasswordViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    val viewState: LiveData<PasswordViewState>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData<PasswordViewState>(PasswordViewState.Idle)

    fun validatePassword(password: String) {
        viewModelScope.launch {
            mutableViewState.postValue(PasswordViewState.Loading)
            val result = passwordRepository.validatePassword(password)
            mutableViewState.postValue(
                if (result) PasswordViewState.PasswordCorrect else PasswordViewState.PasswordIncorrect
            )
        }
    }

    fun incorrectPasswordDialogDismissed() {
        mutableViewState.value = PasswordViewState.Idle
    }
}