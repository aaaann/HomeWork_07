package otus.homework.customview

import java.util.concurrent.TimeUnit

object SpendingLineGraphHelper {

    const val SPENDING_INTERVAL = 5_000

    /**
     * подсчитать кол-во меток по оси Y
     */
    fun calculateYMarksCount(spending: List<CategorySpending>): Int {
        val maxSpending = spending.maxOf { it.amount }
        return (maxSpending / SPENDING_INTERVAL).toInt() + 2
    }

    fun calculateXMarksCount(spending: List<CategorySpending>): Int {
        val minDate = spending.minOf { it.date }
        val maxDate = spending.maxOf { it.date }
        return TimeUnit.MILLISECONDS.toDays(maxDate - minDate).toInt() + 1
    }
}