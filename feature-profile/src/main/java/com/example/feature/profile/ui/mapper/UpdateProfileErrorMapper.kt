package com.example.feature.profile.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.feature.profile.R

fun UpdateProfileError.toUiText(resources: Resources): String {
    return when (this) {
        is UpdateProfileError.UserIsNotAuthorized -> resources.getString(R.string.error_user_not_authorized)
        is UpdateProfileError.InvalidFile -> resources.getString(R.string.error_invalid_file)
        is UpdateProfileError.Network -> resources.getString(R.string.error_no_network)
        is UpdateProfileError.FailedToGetImage -> resources.getString(R.string.error_failed_to_get_image)
        is UpdateProfileError.Unknown -> resources.getString(R.string.error_unknown_error) + this.error
        is UpdateProfileError.NoChanges ->resources.getString(R.string.error_no_changes_to_save)
    }
}
