package otus.homework.customview

object SpendingLineGraphHelper {

    const val SPENDING_INTERVAL = 5_000

    /**
     * подсчитать кол-во меток по оси Y
     */
    fun calculateYMarksCount(spending: List<CategorySpending>): Int {
        val maxSpending = spending.maxOf { it.amount }
        return (maxSpending / SPENDING_INTERVAL).toInt() + 2
    }
}