package com.example.fitcoach.repository

import com.example.fitcoach.data.model.Routine

object RoutineRepository {
    private val routines = mutableListOf<Routine>()

    fun addRoutine(routine: Routine) {
        routines.add(routine)
    }

    fun getAllRoutines(): List<Routine> = routines

    fun getRoutineById(id: String): Routine? = routines.find { it.id == id }
}
