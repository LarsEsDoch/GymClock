package de.lars.gymclock.data

import de.lars.gymclock.db.WorkoutSet
import de.lars.gymclock.db.WorkoutSetDao
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutSetDao: WorkoutSetDao) {

    val allSets: Flow<List<WorkoutSet>> = workoutSetDao.getAll()

    val latestSet: Flow<WorkoutSet?> = workoutSetDao.getLatest()

    suspend fun insertSet() {
        workoutSetDao.insert(WorkoutSet())
    }
}
