package models

data class OAuth2Token(
    val accessToken: String,
    val refreshToken: String,
)
