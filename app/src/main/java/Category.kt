object CategoryList{//this is how we can access data everywhere
    val categroyList = mutableListOf<Category>()
}
data class Category(
    val categoryName: String,
    val color: String,
)

// Hard coded User details
// This will be added to the Fire base database in the next part for better security
/*object TaskData {
    val taskEntry = mutableListOf<Task>(

    )
}*/