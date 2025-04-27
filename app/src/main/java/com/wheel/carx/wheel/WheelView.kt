package com.wheel.carx.wheel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.wheel.carx.model.WheelSegment
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class WheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var segments: List<WheelSegment> = emptyList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
    }
    private val diamondPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }
    
    private val tempRect = RectF()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var textSize = 24f
    
    init {
        // Set up the paints
        paint.style = Paint.Style.FILL
        textPaint.textSize = textSize
    }
    
    fun setSegments(segments: List<WheelSegment>) {
        this.segments = segments
        invalidate()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(centerX, centerY) * 0.9f // 90% of the smallest dimension
        
        // Adjust text size based on wheel size
        textSize = radius * 0.06f
        textPaint.textSize = textSize
        
        tempRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (segments.isEmpty()) return
        
        // Draw wheel segments
        val anglePerSegment = 360f / segments.size
        for (i in segments.indices) {
            val startAngle = i * anglePerSegment
            paint.color = segments[i].color
            canvas.drawArc(tempRect, startAngle, anglePerSegment, true, paint)
        }
        
        // Draw the outer ring
        canvas.drawCircle(centerX, centerY, radius, strokePaint)
        
        // Draw dots at segment boundaries
        val dotRadius = radius * 0.02f
        for (i in 0 until segments.size) {
            val angle = Math.toRadians((i * anglePerSegment).toDouble())
            val dotX = centerX + (radius * 0.95f * sin(angle)).toFloat()
            val dotY = centerY - (radius * 0.95f * cos(angle)).toFloat()
            canvas.drawCircle(dotX, dotY, dotRadius, dotPaint)
        }
        
        // Draw the text for each segment
        for (i in segments.indices) {
            val angle = Math.toRadians(((i * anglePerSegment) + (anglePerSegment / 2)).toDouble())
            val textX = centerX + (radius * 0.7f * sin(angle)).toFloat()
            val textY = centerY - (radius * 0.7f * cos(angle)).toFloat() + (textPaint.textSize / 3)
            
            // Rotate text to be tangent to the wheel
            canvas.save()
            canvas.rotate(
                (i * anglePerSegment) + (anglePerSegment / 2) + 90,
                textX,
                textY
            )
            canvas.drawText(segments[i].text, textX, textY, textPaint)
            canvas.restore()
        }
        
        // Draw the center circle (coin)
        val centerRadius = radius * 0.15f
        canvas.drawCircle(centerX, centerY, centerRadius, centerPaint)
        
        // Draw a recycling icon in the center
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            alpha = 150
        }
        val iconSize = centerRadius * 0.5f
        canvas.drawCircle(centerX, centerY, iconSize, iconPaint)
        
        // Draw arrow (diamond) at the top
        val diamondPath = Path()
        val diamondSize = radius * 0.1f
        diamondPath.moveTo(centerX, centerY - radius - diamondSize)
        diamondPath.lineTo(centerX - diamondSize, centerY - radius)
        diamondPath.lineTo(centerX, centerY - radius + diamondSize)
        diamondPath.lineTo(centerX + diamondSize, centerY - radius)
        diamondPath.close()
        canvas.drawPath(diamondPath, diamondPaint)
    }
}
