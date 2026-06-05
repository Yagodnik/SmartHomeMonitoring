package models

data class Account(
    val username: String,
    val email: String,
) {
    companion object {
        fun empty() = Account("", "")
    }
}
