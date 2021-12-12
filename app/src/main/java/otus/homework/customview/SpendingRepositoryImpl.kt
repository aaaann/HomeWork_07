package otus.homework.customview

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import otus.homework.customview.Category.Companion.toCategory

class SpendingRepositoryImpl(private val resourceWrapper: ResourceWrapper) : SpendingRepository {
    override fun getCategories(): List<CategoryItem> {
        return getSpending()
            .map { CategoryItem(it.category.toCategory(), it.amount) }
            .groupingBy { it.category }.reduce { _, accumulator, element ->
                CategoryItem(
                    accumulator.category,
                    accumulator.amount + element.amount
                )
            }.values.toList()
    }

    private fun getSpending(): List<SpendingModel> {
        val spendingString = resourceWrapper.openRawResource(R.raw.payload)
        return Gson().fromJson(spendingString, object : TypeToken<List<SpendingModel>>() {}.type)
    }
}