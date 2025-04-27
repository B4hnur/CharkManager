package com.wheel.carx

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wheel.carx.admin.AdminActivity
import com.wheel.carx.dialogs.SegmentDialog
import com.wheel.carx.model.WheelSegment
import com.wheel.carx.utils.PreferenceManager
import com.wheel.carx.wheel.WheelView
import java.util.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var wheelView: WheelView
    private lateinit var spinButton: Button
    private lateinit var addSegmentButton: Button
    private lateinit var preferenceManager: PreferenceManager
    
    private val segments = mutableListOf<WheelSegment>()
    private var isSpinning = false
    
    // Secret tap pattern for admin panel
    private val tapPattern = mutableListOf<Long>()
    private val secretPattern = listOf(300L, 300L, 600L)
    private var lastTapTime = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        preferenceManager = PreferenceManager(this)
        
        wheelView = findViewById(R.id.wheelView)
        spinButton = findViewById(R.id.spinButton)
        addSegmentButton = findViewById(R.id.addSegmentButton)
        
        // Load saved segments
        loadSegments()
        
        spinButton.setOnClickListener {
            if (!isSpinning) {
                spinWheel()
            }
        }
        
        addSegmentButton.setOnClickListener {
            showAddSegmentDialog()
        }
        
        // Long press on app title to open admin panel
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setOnLongClickListener {
            showAdminAccessDialog()
            true
        }
    }
    
    private fun loadSegments() {
        segments.clear()
        val savedSegments = preferenceManager.getSegments()
        if (savedSegments.isNotEmpty()) {
            segments.addAll(savedSegments)
        } else {
            // Add default segments if none exist
            segments.addAll(
                listOf(
                    WheelSegment("1000 İZLƏMƏ", android.graphics.Color.BLUE),
                    WheelSegment("2000 İZLƏMƏ", android.graphics.Color.RED),
                    WheelSegment("3000 İZLƏMƏ", android.graphics.Color.BLUE),
                    WheelSegment("BOŞ", android.graphics.Color.RED),
                    WheelSegment("5000 İZLƏMƏ", android.graphics.Color.BLUE),
                    WheelSegment("6000 İZLƏMƏ", android.graphics.Color.RED),
                    WheelSegment("10.000 İZLƏMƏ", android.graphics.Color.BLUE),
                    WheelSegment("BOŞ", android.graphics.Color.RED),
                    WheelSegment("20.000 İZLƏMƏ", android.graphics.Color.BLUE),
                    WheelSegment("BOŞ", android.graphics.Color.RED),
                    WheelSegment("30", android.graphics.Color.GREEN),
                    WheelSegment("29", android.graphics.Color.YELLOW),
                    WheelSegment("28", android.graphics.Color.parseColor("#FFA500")),
                    WheelSegment("27", android.graphics.Color.MAGENTA),
                    WheelSegment("26", android.graphics.Color.parseColor("#FF69B4")),
                    WheelSegment("25", android.graphics.Color.parseColor("#800000")),
                    WheelSegment("24", android.graphics.Color.parseColor("#FF1493")),
                    WheelSegment("23", android.graphics.Color.parseColor("#9400D3")),
                    WheelSegment("22", android.graphics.Color.parseColor("#4169E1")),
                    WheelSegment("21", android.graphics.Color.parseColor("#FF7F50")),
                    WheelSegment("20", android.graphics.Color.BLACK),
                    WheelSegment("19", android.graphics.Color.GRAY),
                    WheelSegment("18", android.graphics.Color.LTGRAY),
                    WheelSegment("17", android.graphics.Color.parseColor("#8B4513")),
                    WheelSegment("16", android.graphics.Color.parseColor("#D2B48C")),
                    WheelSegment("15", android.graphics.Color.parseColor("#FFC0CB")),
                    WheelSegment("14", android.graphics.Color.parseColor("#FF69B4")),
                    WheelSegment("13", android.graphics.Color.parseColor("#FF4500"))
                )
            )
            preferenceManager.saveSegments(segments)
        }
        
        wheelView.setSegments(segments)
    }
    
    private fun showAddSegmentDialog() {
        SegmentDialog(this, null) { segment ->
            segments.add(segment)
            preferenceManager.saveSegments(segments)
            wheelView.setSegments(segments)
        }.show()
    }
    
    private fun showEditSegmentDialog(segment: WheelSegment, position: Int) {
        SegmentDialog(this, segment) { updatedSegment ->
            segments[position] = updatedSegment
            preferenceManager.saveSegments(segments)
            wheelView.setSegments(segments)
        }.show()
    }
    
    private fun spinWheel() {
        isSpinning = true
        
        // Get predetermined winner if set
        val predeterminedPosition = preferenceManager.getPredeterminedWinner()
        
        // Calculate the degrees to rotate
        val degreesPerSegment = 360f / segments.size
        val random = Random()
        val targetPosition = if (predeterminedPosition != -1) predeterminedPosition else random.nextInt(segments.size)
        
        // Randomize the number of complete spins (3-5 spins)
        val spinCount = 3 + random.nextInt(3)
        val degrees = (360 * spinCount) + (targetPosition * degreesPerSegment)
        
        // Create rotation animation
        val rotateAnimation = RotateAnimation(
            0f, degrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        
        // Set animation properties
        rotateAnimation.duration = 3000
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            
            override fun onAnimationEnd(animation: Animation?) {
                // Show result
                val winningPosition = (targetPosition + segments.size) % segments.size
                val winningSegment = segments[winningPosition]
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.winner_message, winningSegment.text),
                    Toast.LENGTH_LONG
                ).show()
                
                // Reset predetermined winner
                if (predeterminedPosition != -1) {
                    preferenceManager.setPredeterminedWinner(-1)
                }
                
                isSpinning = false
            }
            
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        
        // Start the animation
        wheelView.startAnimation(rotateAnimation)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Detect secret tap pattern for admin access
        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()
            
            if (lastTapTime > 0) {
                val interval = currentTime - lastTapTime
                tapPattern.add(interval)
                
                // Check if the pattern matches
                if (tapPattern.size == secretPattern.size) {
                    var patternMatches = true
                    for (i in secretPattern.indices) {
                        val diff = Math.abs(tapPattern[i] - secretPattern[i])
                        if (diff > 150) { // Allow 150ms tolerance
                            patternMatches = false
                            break
                        }
                    }
                    
                    if (patternMatches) {
                        showAdminAccessDialog()
                    }
                    
                    tapPattern.clear()
                }
            }
            
            lastTapTime = currentTime
            
            // Reset pattern after 2 seconds of inactivity
            if (tapPattern.size > 0 && currentTime - lastTapTime > 2000) {
                tapPattern.clear()
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    private fun showAdminAccessDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.admin_access)
            .setMessage(R.string.enter_password)
            .setView(android.widget.EditText(this).apply {
                hint = getString(R.string.password_hint)
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            })
            .setPositiveButton(R.string.login) { dialog, _ ->
                val editText = (dialog as androidx.appcompat.app.AlertDialog)
                    .findViewById<android.widget.EditText>(android.R.id.edit)
                
                val password = editText?.text.toString()
                if (password == "admin1234") { // Simple hardcoded password for demo
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        dialog.show()
    }
}
