package otus.homework.customview

interface SpendingRepository {
    fun getCategories(): List<CategoryItem>
}