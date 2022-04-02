package otus.homework.customview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        findViewById<Button>(R.id.btn_detailed_graph).setOnClickListener {
            startActivity(Intent(this, SpendingDetailsActivity::class.java))
        }
    }
}