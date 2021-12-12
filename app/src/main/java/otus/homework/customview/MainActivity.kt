package otus.homework.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = SpendingRepositoryImpl(ResourceWrapperImpl(applicationContext))

        val pieChart = findViewById<PieChartView>(R.id.pie_chart)
        pieChart.setData(repository.getCategories())
        pieChart.setSectorClickListener(object : PieChartView.OnSectorClickListener {
            override fun onSectorSelect(category: CategoryItem) {
                CategorySpendingBottomSheet.newInstance(category)
                    .show(supportFragmentManager, CategorySpendingBottomSheet.TAG)
            }
        })
    }
}