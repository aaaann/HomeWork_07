package otus.homework.customview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryItem(
    val category: Category,
    val amount: Float
) : Parcelable
