package com.bodycalc.platepilot.data.local

import androidx.room.*
import com.bodycalc.platepilot.data.model.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterLogsByDate(date: String): Flow<List<WaterLog>>
    
    @Query("SELECT SUM(amount) FROM water_logs WHERE date = :date")
    fun getTotalWaterForDate(date: String): Flow<Int?>
    
    @Insert
    suspend fun insertWaterLog(waterLog: WaterLog): Long
    
    @Delete
    suspend fun deleteWaterLog(waterLog: WaterLog)
    
    @Query("DELETE FROM water_logs WHERE date = :date")
    suspend fun deleteAllForDate(date: String)
}
