package com.wheel.carx.admin

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wheel.carx.R
import com.wheel.carx.model.WheelSegment
import com.wheel.carx.utils.PreferenceManager

class AdminActivity : AppCompatActivity() {

    private lateinit var segmentRadioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button
    private lateinit var preferenceManager: PreferenceManager
    private var segments = mutableListOf<WheelSegment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        
        // Set up toolbar with back button
        setSupportActionBar(findViewById(R.id.adminToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.admin_panel)
        
        segmentRadioGroup = findViewById(R.id.segmentsRadioGroup)
        saveButton = findViewById(R.id.saveButton)
        clearButton = findViewById(R.id.clearButton)
        
        preferenceManager = PreferenceManager(this)
        
        // Load segments
        segments = preferenceManager.getSegments().toMutableList()
        loadSegmentOptions()
        
        // Get currently set predetermined winner
        val currentWinner = preferenceManager.getPredeterminedWinner()
        if (currentWinner != -1 && currentWinner < segmentRadioGroup.childCount) {
            (segmentRadioGroup.getChildAt(currentWinner) as? RadioButton)?.isChecked = true
        }
        
        saveButton.setOnClickListener {
            val selectedId = segmentRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                // Find the index of the selected radio button
                for (i in 0 until segmentRadioGroup.childCount) {
                    val radioButton = segmentRadioGroup.getChildAt(i) as RadioButton
                    if (radioButton.id == selectedId) {
                        preferenceManager.setPredeterminedWinner(i)
                        Toast.makeText(
                            this,
                            getString(R.string.winner_set, segments[i].text),
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
            }
        }
        
        clearButton.setOnClickListener {
            preferenceManager.setPredeterminedWinner(-1)
            segmentRadioGroup.clearCheck()
            Toast.makeText(this, R.string.winner_cleared, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadSegmentOptions() {
        segmentRadioGroup.removeAllViews()
        
        segments.forEachIndexed { index, segment ->
            val radioButton = RadioButton(this)
            radioButton.id = index
            radioButton.text = segment.text
            
            // Add to radio group
            segmentRadioGroup.addView(radioButton)
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
