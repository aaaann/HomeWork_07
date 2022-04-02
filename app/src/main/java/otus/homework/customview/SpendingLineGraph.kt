package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.use

class SpendingLineGraph(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var xMarksCount = 0
    private var yMarksCount = 0
    private val markersXCoordinates = mutableListOf<Float>()
    private val markersWithColors = mutableListOf<Pair<Int, List<PointF>>>()
    private var spendingInterval = 5000f //TODO: maybe add to styleable attrs

    private var xMarkInterval = 0
    private var yMarkInterval = 0
    private var axisPaintWidth = 0f

    var realXAxisLength = 0
    var realYAxisLength = 0

    @ColorInt
    private var axisPaintColor = 0

    @ColorInt
    private var xGridPaintColor = 0

    @ColorInt
    private var yGridPaintColor = 0

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val verticalGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(
            floatArrayOf(30F, 10F),
            0F
        )
    }

    private val horizontalGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(
            floatArrayOf(30F, 5F, 5F, 5F),
            0F
        )
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.SpendingLineGraph).use { typedArray ->
            axisPaintColor = typedArray.getColor(
                R.styleable.SpendingLineGraph_axis_color,
                context.resources.getColor(R.color.black, context.theme)
            )
            xGridPaintColor = typedArray.getColor(
                R.styleable.SpendingLineGraph_axis_color,
                context.resources.getColor(R.color.blue_gray_400, context.theme)
            )
            yGridPaintColor = typedArray.getColor(
                R.styleable.SpendingLineGraph_axis_color,
                context.resources.getColor(R.color.blue_gray_400, context.theme)
            )
        }

        xMarkInterval = context.resources.getDimensionPixelSize(R.dimen.x_mark_interval)
        yMarkInterval = context.resources.getDimensionPixelSize(R.dimen.y_mark_interval)
        axisPaintWidth = context.resources.getDimension(R.dimen.axis_paint_width)

        configurePaints()
    }

    private fun configurePaints() {
        axisPaint.apply {
            strokeWidth = axisPaintWidth
            color = axisPaintColor
        }

        verticalGridPaint.apply {
            color = xGridPaintColor
        }

        horizontalGridPaint.apply {
            color = yGridPaintColor
        }

        linePaint.apply {
            strokeWidth = axisPaintWidth * 2
        }
    }

    fun setData() {
        // calculate xMarksCount, yMarksCount
        xMarksCount = 6 // TODO: calculate from data
        yMarksCount = 6 // TODO: calculate from data
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(
            TAG, "onMeasure() called with: " +
                "widthMeasureSpec = ${MeasureSpec.toString(widthMeasureSpec)}, " +
                "heightMeasureSpec = ${MeasureSpec.toString(heightMeasureSpec)}"
        )

        val resultWidth: Int
        val resultHeight: Int

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        resultWidth = if (widthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            xMarkInterval * (xMarksCount - 1) + paddingStart + paddingEnd
        }


        resultHeight = if (heightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            yMarkInterval * (yMarksCount - 1) + paddingTop + paddingEnd
        }

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        realXAxisLength = measuredWidth - paddingStart - paddingEnd
        realYAxisLength = measuredHeight - paddingTop - paddingBottom

        xMarkInterval = realXAxisLength / (xMarksCount - 1)
        yMarkInterval = realYAxisLength / (yMarksCount - 1)

        calculateMarkersXCoordinates()
        fillMarkersWithColors()
    }

    private fun calculateMarkersXCoordinates() {
        for (i in 0 until xMarksCount) {
            markersXCoordinates.add((paddingStart + xMarkInterval * i).toFloat())
        }
    }

    private fun fillMarkersWithColors() {
        // TODO: create based on real data
        val categoriesSpending = mapOf(
            R.color.red_700 to mapOf(
                0 to 5100f,
                1 to 0f,
                2 to 0f,
                3 to 12456f,
                4 to 6080f,
                5 to 500f
            ),
            R.color.light_green_200 to mapOf(
                0 to 800f,
                1 to 3800f,
                2 to 45f,
                3 to 2367f,
                4 to 0f,
                5 to 1680f
            )
        )
        categoriesSpending.forEach {
            calculateMarkers(it.key, it.value)
        }
    }

    private fun calculateMarkers(colorRes: Int, categorySpending: Map<Int, Float>) {
        val categoryMarkers = mutableListOf<PointF>()
        markersXCoordinates.forEachIndexed { index, xPos ->
            // найти трату на дату [index]
            val daySpending = categorySpending[index] ?: 0f

            val yPos = paddingTop + realYAxisLength - ((daySpending / spendingInterval) * yMarkInterval)
            categoryMarkers.add(PointF(xPos, yPos))
        }
        markersWithColors.add(Pair(colorRes, categoryMarkers))
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "measuredWidth: $measuredWidth, measuredHeight: $measuredHeight")

        canvas?.apply {
            drawAxis()
            drawGrid()
            drawAllLines()
        }
    }

    private fun Canvas.drawAxis() {
        drawLine(
            paddingStart.toFloat(),
            paddingTop + realYAxisLength.toFloat(),
            paddingStart + realXAxisLength.toFloat(),
            paddingTop + realYAxisLength.toFloat(),
            axisPaint
        )
        drawLine(
            paddingStart + realXAxisLength.toFloat(),
            paddingTop.toFloat(),
            paddingStart + realXAxisLength.toFloat(),
            paddingTop + realYAxisLength.toFloat(),
            axisPaint
        )
    }

    private fun Canvas.drawGrid() {
        // draw vertical grid
        var startX = paddingStart + xMarkInterval
        repeat(xMarksCount - 2) {
            drawLine(
                startX.toFloat(),
                paddingTop.toFloat(),
                startX.toFloat(),
                paddingTop + realYAxisLength.toFloat(),
                verticalGridPaint
            )
            startX += xMarkInterval
        }

        // draw horizontal grid
        var startY = paddingStart + yMarkInterval
        repeat(yMarksCount - 2) {
            drawLine(
                paddingStart.toFloat(),
                startY.toFloat(),
                paddingStart + realXAxisLength.toFloat(),
                startY.toFloat(),
                horizontalGridPaint
            )
            startY += yMarkInterval
        }
    }

    private fun Canvas.drawAllLines() {
        markersWithColors.forEach {
            drawMarkersAndLine(it.first, it.second)
        }
    }

    private fun Canvas.drawMarkersAndLine(@ColorRes lineColor: Int, markers: List<PointF>) {
        linePaint.apply { color = context.resources.getColor(lineColor, context.theme) }
        var previousMarker: PointF? = null
        for (marker in markers) {
            if (previousMarker != null) {
                // draw the line
                drawLine(previousMarker.x, previousMarker.y, marker.x, marker.y, linePaint)
            }

            previousMarker = marker
            // draw the marker
            drawCircle(
                marker.x,
                marker.y,
                5f,
                linePaint
            )
        }
    }

    private companion object {
        const val TAG = "SpendingLineGraph"
    }
}