package de.lars.gymclock.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSet)

    @Query("SELECT * FROM workout_sets ORDER BY timestamp DESC")
    fun getAll(): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(): Flow<WorkoutSet?>
}
