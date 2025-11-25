package com.bodycalc.platepilot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.model.UserProfile

@Database(
    entities = [Meal::class, MealPlan::class, UserProfile::class],
    version = 12,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PlatePilotDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun userProfileDao(): UserProfileDao
    
    companion object {
        @Volatile
        private var INSTANCE: PlatePilotDatabase? = null
        
        fun getDatabase(context: Context): PlatePilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlatePilotDatabase::class.java,
                    "plate_pilot_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
