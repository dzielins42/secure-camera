package pl.dzielins42.seccam.ui.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.dzielins42.seccam.data.repo.PasswordRepository
import timber.log.Timber

class PasswordViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    val viewState: LiveData<PasswordViewState>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData(PasswordViewState.Idle)

    fun validatePassword(password: String) {
        viewModelScope.launch {
            mutableViewState.postValue(PasswordViewState.Loading)
            mutableViewState.postValue(
                try {
                    val result = passwordRepository.validatePassword(password)
                    if (result) PasswordViewState.PasswordCorrect else PasswordViewState.PasswordIncorrect
                } catch (e: Exception) {
                    // Nothing can be done about the exception, but it should be handled graciously
                    Timber.e(e)
                    PasswordViewState.PasswordIncorrect
                }
            )
        }
    }

    fun incorrectPasswordDialogDismissed() {
        mutableViewState.value = PasswordViewState.Idle
    }
}