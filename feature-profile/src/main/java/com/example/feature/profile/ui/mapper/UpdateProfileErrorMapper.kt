package com.example.feature.profile.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.feature.profile.R

fun UpdateProfileError.toUiText(resources: Resources): String {
    return when (this) {
        UpdateProfileError.UserIsNotAuthorized -> resources.getString(R.string.error_user_not_authorized)
        UpdateProfileError.InvalidFile -> resources.getString(R.string.error_invalid_file)
        UpdateProfileError.Network -> resources.getString(R.string.error_no_network)
        UpdateProfileError.FailedToGetImage -> resources.getString(R.string.error_failed_to_get_image)
        is UpdateProfileError.Unknow -> resources.getString(R.string.error_unknown_error) + this.error
    }
}
