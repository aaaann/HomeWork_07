package otus.homework.customview

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import otus.homework.customview.Category.Companion.toCategory
import java.util.Calendar

class SpendingRepositoryImpl(private val resourceWrapper: ResourceWrapper) : SpendingRepository {
    override fun getCategoriesOverallSpending(): List<CategoryOverallSpending> {
        return getSpending()
            .map { CategoryOverallSpending(it.category.toCategory(), it.amount) }
            .groupingBy { it.category }.reduce { _, accumulator, element ->
                CategoryOverallSpending(
                    accumulator.category,
                    accumulator.amount + element.amount
                )
            }.values.toList()
    }

    override fun getCategoriesSpendingPerDate(): List<CategorySpending> {
        return getSpending()
            .map { CategorySpending(it.category.toCategory(), it.amount, (it.time * 1000).toDateWithoutTime()) }
            .groupingBy { listOf(it.category, it.date) }.reduce { _, accumulator, element ->
                CategorySpending(
                    accumulator.category,
                    accumulator.amount + element.amount,
                    accumulator.date
                )
            }.values.toList()
    }

    private fun getSpending(): List<SpendingModel> {
        val spendingString = resourceWrapper.openRawResource(R.raw.payload)
        return Gson().fromJson(spendingString, object : TypeToken<List<SpendingModel>>() {}.type)
    }

    private fun Long.toDateWithoutTime(): Long = Calendar.getInstance()
        .also { calendar ->
            calendar.timeInMillis = this
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}