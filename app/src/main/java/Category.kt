object CategoryList{
    val categoryList = mutableListOf<Category>()

}
data class Category(
    val categoryName: String,
    val color: String,
)