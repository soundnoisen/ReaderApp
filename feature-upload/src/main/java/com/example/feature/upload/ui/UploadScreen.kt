package com.example.feature.upload.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core.ui.component.BaseHeader
import com.example.core.ui.component.BaseSnackBar
import com.example.feature.upload.R
import com.example.feature.upload.data.util.queryFileName
import com.example.feature.upload.ui.component.FileSelection
import com.example.feature.upload.ui.component.UploadBottomSheet
import com.example.feature.upload.ui.mapper.toUiText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(viewModel: UploadViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = queryFileName(context, it)
            viewModel.handleIntent(UploadIntent.FileSelected(it, fileName))
        }
    }


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is UploadEffect.OpenFilePicker -> fileLauncher.launch("*/*")
                is UploadEffect.ShowError -> { snackBarHostState.showSnackbar(effect.error.toUiText(context.resources)) }
                is UploadEffect.UploadSuccessToast -> Toast.makeText(context, context.resources.getString(R.string.msg_download_success), Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold(
        snackbarHost = {
            BaseSnackBar(
                snackBarHostState = snackBarHostState,
            )
        },
        topBar = { BaseHeader(
            text = stringResource(R.string.title_download),
            modifier =  Modifier.fillMaxWidth().padding(top = 30.dp))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            FileSelection(
                uploading = state.isBottomSheetVisible,
                onClick = { viewModel.handleIntent(UploadIntent.SelectFile) },
            )
            Spacer(Modifier.height(20.dp))
        }

        if (state.isBottomSheetVisible) {
            UploadBottomSheet(
                state = state,
                sheetState = sheetState,
                onTitleChange = { viewModel.handleIntent(UploadIntent.TitleChanged(it)) },
                onAuthorChange = { viewModel.handleIntent(UploadIntent.AuthorChanged(it)) },
                onUploadClicked = { viewModel.handleIntent(UploadIntent.UploadClicked) },
                onDismiss = { viewModel.handleIntent(UploadIntent.ClearSelectedFile)}
            )
        }
    }
}

