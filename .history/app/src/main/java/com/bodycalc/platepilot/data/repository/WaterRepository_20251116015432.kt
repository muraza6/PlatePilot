package com.bodycalc.platepilot.data.repository

import com.bodycalc.platepilot.data.local.WaterLogDao
import com.bodycalc.platepilot.data.model.WaterLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class WaterRepository(private val waterLogDao: WaterLogDao) {
    
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>> {
        return waterLogDao.getWaterLogsByDate(date)
    }
    
    fun getTotalWaterForDate(date: String): Flow<Int?> {
        return waterLogDao.getTotalWaterForDate(date)
    }
    
    suspend fun addWater(amount: Int, date: String = LocalDate.now().toString()): Long {
        return waterLogDao.insertWaterLog(
            WaterLog(
                date = date,
                amount = amount
            )
        )
    }
    
    suspend fun deleteWaterLog(waterLog: WaterLog) {
        waterLogDao.deleteWaterLog(waterLog)
    }
    
    suspend fun resetDailyWater(date: String) {
        waterLogDao.deleteAllForDate(date)
    }
}
