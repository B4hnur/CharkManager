package com.wheel.carx.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.wheel.carx.R
import com.wheel.carx.model.WheelSegment
import kotlin.random.Random

class SegmentDialog(
    context: Context,
    private val segment: WheelSegment?,
    private val onSave: (WheelSegment) -> Unit
) : Dialog(context) {

    private lateinit var nameEditText: EditText
    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var colorPreview: TextView
    
    private var currentColor = segment?.color ?: getRandomColor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_segment)
        
        nameEditText = findViewById(R.id.nameEditText)
        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)
        colorPreview = findViewById(R.id.colorPreview)
        
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        
        // Set title based on whether we're editing or adding
        setTitle(if (segment == null) R.string.add_segment else R.string.edit_segment)
        
        // If editing existing segment, populate the fields
        segment?.let {
            nameEditText.setText(it.text)
            setupColorSeekBars(it.color)
        } ?: setupColorSeekBars(currentColor)
        
        // Only show delete button when editing
        deleteButton.visibility = if (segment == null) android.view.View.GONE else android.view.View.VISIBLE
        
        // Set up the color seek bars
        val colorListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateColorPreview()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }
        
        redSeekBar.setOnSeekBarChangeListener(colorListener)
        greenSeekBar.setOnSeekBarChangeListener(colorListener)
        blueSeekBar.setOnSeekBarChangeListener(colorListener)
        
        // Button click listeners
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                onSave(WheelSegment(name, getCurrentColor()))
                dismiss()
            } else {
                nameEditText.error = context.getString(R.string.error_empty_name)
            }
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        deleteButton.setOnClickListener {
            // Only for existing segments
            segment?.let {
                // Remove from list - handled by parent activity
                onSave(it.copy(isDeleted = true))
                dismiss()
            }
        }
    }
    
    private fun setupColorSeekBars(color: Int) {
        redSeekBar.progress = Color.red(color)
        greenSeekBar.progress = Color.green(color)
        blueSeekBar.progress = Color.blue(color)
        updateColorPreview()
    }
    
    private fun updateColorPreview() {
        val color = getCurrentColor()
        colorPreview.setBackgroundColor(color)
        currentColor = color
    }
    
    private fun getCurrentColor(): Int {
        return Color.rgb(
            redSeekBar.progress,
            greenSeekBar.progress,
            blueSeekBar.progress
        )
    }
    
    private fun getRandomColor(): Int {
        return Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
    }
}
