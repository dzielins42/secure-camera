package pl.dzielins42.seccam.ui.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.dzielins42.bloxyz.lce.LceViewState
import pl.dzielins42.seccam.data.repo.PasswordRepository
import timber.log.Timber

class PasswordViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    val viewState: LiveData<LceViewState<PasswordViewState>>
        get() = mutableViewState
    private val mutableViewState =
        MutableLiveData(LceViewState.content(PasswordViewState.Idle))

    fun validatePassword(password: String) {
        viewModelScope.launch {
            mutableViewState.postValue(LceViewState.loading())
            mutableViewState.postValue(
                try {
                    val result = passwordRepository.validatePassword(password)
                    LceViewState.content(
                        if (result) PasswordViewState.PasswordCorrect
                        else PasswordViewState.PasswordIncorrect
                    )
                } catch (e: Exception) {
                    // Nothing can be done about the exception, but it should be handled graciously
                    Timber.e(e)
                    LceViewState.content(PasswordViewState.PasswordIncorrect)
                }
            )
        }
    }

    fun incorrectPasswordDialogDismissed() {
        mutableViewState.value = LceViewState.content(PasswordViewState.Idle)
    }
}