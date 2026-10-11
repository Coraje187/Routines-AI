package com.familyroutines.ai

import com.familyroutines.data.local.entities.ParentNoteEntity

object PromptBuilder {
    fun buildGemmaPrompt(childName: String, notes: List<ParentNoteEntity>, question: String): String {
        val formattedNotes = if (notes.isEmpty()) {
            "- No specific notes recorded yet."
        } else {
            notes.joinToString(separator = "\n") { note ->
                "- [Category: ${note.category.uppercase()} | By: ${note.author}]: ${note.rawText}"
            }
        }

        return """
            <start_of_turn>user
            You are a calm, factual family assistant. A parent needs instructions to care for their neurodivergent child and prevent sensory meltdowns.
            Answer using ONLY the verified parent notes below. State exact preparation times, quantities, and forbidden actions. If information is missing, state it plainly.

            Verified Notes for $childName:
            $formattedNotes

            Question: $question<end_of_turn>
            <start_of_turn>model
        """.trimIndent()
    }
}
