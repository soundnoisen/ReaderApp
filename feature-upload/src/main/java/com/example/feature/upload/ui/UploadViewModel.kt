package com.example.feature.upload.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.book.UploadError
import com.example.core.domain.model.book.UploadProgress
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature.upload.domain.usecase.UploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val upload: UploadFileUseCase
): ViewModel() {

    private val _state = MutableStateFlow(UploadState())
    val state: StateFlow<UploadState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UploadEffect>()
    val effect: SharedFlow<UploadEffect> = _effect.asSharedFlow()

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
        _state.update{ it.copy(title = title) }
    }

    private fun authorChange(author: String) {
        _state.update{ it.copy(author = author) }
    }

    private fun emitFilePicker() {
        viewModelScope.launch {
            _effect.emit(UploadEffect.OpenFilePicker)
        }
    }

    private fun clear() {
        _state.value = UploadState()
    }

    private fun fileSelect(fileUri: Uri, fileName: String) {
        if (!isValidFile(fileName)) {
            emitError(UploadError.InvalidFile)
            return
        }
        _state.update {
            it.copy(
                uri = fileUri,
                title = fileName.substringBeforeLast('.'),
                isBottomSheetVisible = true
            )
        }
    }


    fun upload() {
        val state = _state.value

        if (state.uri == null || state.title.isBlank() || state.author.isBlank()) {
            emitError(UploadError.InvalidData)
            return
        }

        if (!networkChecker.isNetworkAvailable()) {
            emitError(UploadError.Network)
            return
        }

        viewModelScope.launch {
            upload(state.uri,state.title, state.author).collect { progress ->
                handleUploadProgress(progress)
            }
        }
    }

    private fun handleUploadProgress(progress: UploadProgress) {
        when (progress) {
            is UploadProgress.Uploading -> _state.update { it.copy(progress = progress) }
            is UploadProgress.Success -> {
                _state.update { it.copy(progress = progress) }
                clear()
                viewModelScope.launch { _effect.emit(UploadEffect.UploadSuccessToast) }
            }
            is UploadProgress.Error -> {
                _state.update { it.copy(progress = UploadProgress.Idle) }
                emitError(progress.error)
            }
            is UploadProgress.Idle -> Unit
        }
    }


    private fun emitError(error: UploadError) {
        viewModelScope.launch {
            _effect.emit(UploadEffect.ShowError(error))
        }
    }

    private fun isValidFile(fileName: String): Boolean {
        return fileName
            .substringAfterLast('.', "")
            .lowercase() in setOf("txt", "epub", "pdf")
    }
}