package com.familyroutines.ai

import android.content.Context
import com.familyroutines.data.local.entities.ParentNoteEntity
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FamilyMemoryEngine(private val context: Context) {
    private var llmInference: LlmInference? = null
    var isInitialized: Boolean = false
        private set

    suspend fun initialize(modelFileName: String = "gemma-2b-it.bin"): Boolean = withContext(Dispatchers.IO) {
        val modelFile = File(context.filesDir, modelFileName)
        if (!modelFile.exists()) {
            return@withContext false
        }

        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(384)
                .setPreferredBackend(LlmInference.Backend.GPU) // Optimal on Mali-G57 (Fire Max 11)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            isInitialized = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun askAssistant(
        childName: String,
        notes: List<ParentNoteEntity>,
        question: String
    ): String = withContext(Dispatchers.Default) {
        if (!isInitialized || llmInference == null) {
            return@withContext fallbackSearch(notes, question)
        }

        val prompt = PromptBuilder.buildGemmaPrompt(childName, notes, question)
        try {
            llmInference?.generateResponse(prompt)?.trim() ?: "No response generated."
        } catch (e: Exception) {
            "Inference error: ${e.localizedMessage}"
        }
    }

    private fun fallbackSearch(notes: List<ParentNoteEntity>, question: String): String {
        val terms = question.lowercase().split(" ").filter { it.length > 2 }
        val matches = notes.filter { note ->
            terms.any { note.rawText.lowercase().contains(it) }
        }
        return if (matches.isNotEmpty()) {
            "Matching rules found:\n" + matches.joinToString("\n• ") { it.rawText }
        } else {
            "No verified notes found matching your question."
        }
    }

    fun close() {
        llmInference?.close()
        llmInference = null
        isInitialized = false
    }
}
