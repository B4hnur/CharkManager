package com.wheel.carx.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import com.wheel.carx.model.WheelSegment
import org.json.JSONArray
import org.json.JSONObject

class PreferenceManager(context: Context) {
    
    private val prefs: SharedPreferences = context
        .getSharedPreferences("wheel_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_SEGMENTS = "segments"
        private const val KEY_PREDETERMINED_WINNER = "predetermined_winner"
    }
    
    fun saveSegments(segments: List<WheelSegment>) {
        val jsonArray = JSONArray()
        
        val filteredSegments = segments.filter { !it.isDeleted }
        
        for (segment in filteredSegments) {
            val segmentJson = JSONObject().apply {
                put("text", segment.text)
                put("color", segment.color)
            }
            jsonArray.put(segmentJson)
        }
        
        prefs.edit().putString(KEY_SEGMENTS, jsonArray.toString()).apply()
    }
    
    fun getSegments(): List<WheelSegment> {
        val segmentJson = prefs.getString(KEY_SEGMENTS, null) ?: return emptyList()
        
        return try {
            val segments = mutableListOf<WheelSegment>()
            val jsonArray = JSONArray(segmentJson)
            
            for (i in 0 until jsonArray.length()) {
                val segmentObject = jsonArray.getJSONObject(i)
                segments.add(
                    WheelSegment(
                        segmentObject.getString("text"),
                        segmentObject.getInt("color")
                    )
                )
            }
            
            segments
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun setPredeterminedWinner(position: Int) {
        prefs.edit().putInt(KEY_PREDETERMINED_WINNER, position).apply()
    }
    
    fun getPredeterminedWinner(): Int {
        return prefs.getInt(KEY_PREDETERMINED_WINNER, -1)
    }
}
