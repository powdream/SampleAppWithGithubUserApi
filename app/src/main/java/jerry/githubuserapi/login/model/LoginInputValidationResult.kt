package jerry.githubuserapi.login.model

import android.support.annotation.StringRes
import jerry.githubuserapi.R

enum class LoginInputValidationResult(
    val isEmailAddressFormatInvalid: Boolean = false,
    val isPasswordFormatInvalid: Boolean = false,
    @StringRes
    val errorResId: Int = 0
) {
    SHORT_PASSWORD(
        isPasswordFormatInvalid = true,
        errorResId = R.string.error_invalid_password
    ),
    EMPTY_EMAIL_ADDRESS(
        isEmailAddressFormatInvalid = true,
        errorResId = R.string.error_field_required
    ),
    INVALID_EMAIL_ADDRESS_FORMAT(
        isEmailAddressFormatInvalid = true,
        errorResId = R.string.error_invalid_email
    ),
    OK
}
