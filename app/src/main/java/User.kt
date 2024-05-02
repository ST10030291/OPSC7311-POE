// Data Class
data class UserDetails(
    val email: String,
    val password: String,
    val username: String,
    val dateOfCreation: String
)

// Hard coded User details
// This will be added to the Fire base database in the next part for better security
object UserData {
    val users = mutableListOf<UserDetails>(
        UserDetails("avish@gmail.com", "avish", "avish1", "20/04/2024"),
        UserDetails("kaushil@gmail.com", "kaushil", "kaushil1", "20/04/2024"),
        UserDetails("tivolan@gmail.com", "tivolan", "tivolan1", "20/04/2024"),
        UserDetails("eben@gmail.com", "eben", "eben1", "21/04/2024")
    )
}

