package jerry.githubuserapi.login.model

data class LoginFormSnapshot(
    val email: String,
    val password: String
) {
    fun validate(): LoginInputValidationResult {
        if (email.isEmpty()) {
            return LoginInputValidationResult.EMPTY_EMAIL_ADDRESS
        }

        if (!EMAIL_VALIDATION.matches(email)) {
            return LoginInputValidationResult.INVALID_EMAIL_ADDRESS_FORMAT
        }

        if (password.length < MINIMUM_PASSWORD_LENGTH) {
            return LoginInputValidationResult.SHORT_PASSWORD
        }

        return LoginInputValidationResult.OK
    }

    companion object {
        private val EMAIL_VALIDATION: Regex =
            """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".toRegex()

        private const val MINIMUM_PASSWORD_LENGTH = 4
    }
}
