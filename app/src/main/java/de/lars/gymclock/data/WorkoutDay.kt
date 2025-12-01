package de.lars.gymclock.data

import de.lars.gymclock.db.WorkoutSet

data class WorkoutDay(
    val date: String,
    val sets: List<WorkoutSet>,
    val duration: String
)
