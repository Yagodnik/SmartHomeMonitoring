package models

data class AuthData(
    val challengeCode: String,
    val verificationUrl: String,
)
