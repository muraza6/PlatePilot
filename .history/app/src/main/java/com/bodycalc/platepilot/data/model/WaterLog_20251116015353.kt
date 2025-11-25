package com.bodycalc.platepilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "2024-11-16" format
    val amount: Int, // Amount in ml (e.g., 250ml per glass)
    val timestamp: Long = System.currentTimeMillis()
)
