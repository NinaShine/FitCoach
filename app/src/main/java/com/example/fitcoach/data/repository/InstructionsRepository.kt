package com.example.fitcoach.data.repository

import android.content.Context
import com.example.fitcoach.data.model.ExerciseInstructions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class InstructionsRepository(private val context: Context) {

    private var instructionsCache: Map<String, List<String>>? = null

    fun getInstructions(exerciseId: String): List<String> {
        if (instructionsCache == null) {
            loadInstructionsFromRaw()
        }
        return instructionsCache?.get(exerciseId) ?: getDefaultInstructions()
    }

    private fun loadInstructionsFromRaw() {
        try {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier(
                    "exercise_instructions",
                    "raw",
                    context.packageName
                )
            )

            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val gson = Gson()
            val type = object : TypeToken<Map<String, List<ExerciseInstructions>>>() {}.type
            val data: Map<String, List<ExerciseInstructions>> = gson.fromJson(jsonString, type)

            instructionsCache = data["exercise_instructions"]?.associate {
                it.exerciseId to it.instructions
            } ?: emptyMap()

        } catch (e: IOException) {
            e.printStackTrace()
            instructionsCache = emptyMap()
        }
    }

    private fun getDefaultInstructions(): List<String> {
        return listOf(
            "Positionnez-vous correctement pour l'exercice",
            "Effectuez le mouvement lentement et contrôlé",
            "Gardez une respiration régulière",
            "Maintenez une bonne forme tout au long de l'exercice",
            "Arrêtez si vous ressentez une douleur"
        )
    }
}