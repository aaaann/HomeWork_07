package otus.homework.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SpendingDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spending_details)

        val repository = SpendingRepositoryImpl(ResourceWrapperImpl(applicationContext)) // TODO: inject with DI

        val spendingGraph = findViewById<SpendingLineGraph>(R.id.spending_graph)
        spendingGraph.setData(repository.getCategoriesSpendingPerDate())
    }
}