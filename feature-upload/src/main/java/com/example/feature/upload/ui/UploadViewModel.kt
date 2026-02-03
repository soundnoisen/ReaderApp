package com.example.feature.upload.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.book.UploadError
import com.example.core.domain.model.book.UploadProgress
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature.upload.domain.usecase.UploadFileUseCase
import com.example.feature.upload.domain.validator.UploadValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val networkChecker: NetworkCheckerRepository,
    private val validator: UploadValidator,
    private val upload: UploadFileUseCase
): ViewModel() {

    private val _state = MutableStateFlow(UploadState())
    val state: StateFlow<UploadState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UploadEffect>()
    val effect: SharedFlow<UploadEffect> = _effect.asSharedFlow()

    private var uploadJob: Job? = null

    fun handleIntent(intent: UploadIntent) {
        when(intent) {
            is UploadIntent.AuthorChanged -> authorChange(intent.author)
            is UploadIntent.FileSelected -> fileSelect(intent.uri, intent.fileName)
            is UploadIntent.RetryClicked -> upload()
            is UploadIntent.SelectFile -> emitFilePicker()
            is UploadIntent.TitleChanged -> titleChange(intent.title)
            is UploadIntent.UploadClicked -> upload()
            is UploadIntent.ClearSelectedFile -> clear()
        }
    }

    private fun titleChange(title: String) {
        val error = validator.validateField(title)
        _state.update{ it.copy(title = title, titleError = error) }
    }

    private fun authorChange(author: String) {
        val error = validator.validateField(author)
        _state.update{ it.copy(author = author, authorError = error) }
    }

    private fun emitFilePicker() {
        sendEffect(UploadEffect.OpenFilePicker)
    }

    private fun clear() {
        _state.value = UploadState()
    }

    private fun fileSelect(fileUri: Uri, fileName: String) {

        validator.validExtension(fileName)?.let {
            return emitError(it)
        }

        _state.update {
            it.copy(
                uri = fileUri,
                title = fileName.substringBeforeLast('.'),
                isBottomSheetVisible = true
            )
        }
    }

    private fun upload() {
        val state = _state.value

        if (state.uri == null || state.authorError != null || state.titleError != null)
            return emitError(UploadError.InvalidData)

        if (!networkChecker.isNetworkAvailable()) return emitError(UploadError.Network)

        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            upload(state.uri,state.title, state.author).collect { progress ->
                _state.update { it.copy(progress = progress) }
                when (progress) {
                    is UploadProgress.Success -> {
                        clear()
                        sendEffect(UploadEffect.UploadSuccess)
                    }
                    is UploadProgress.Error -> emitError(progress.error)
                    else -> Unit
                }
            }
        }
    }

    private fun emitError(error: UploadError) {
        sendEffect(UploadEffect.ShowError(error))
    }

    private fun sendEffect(effect: UploadEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}