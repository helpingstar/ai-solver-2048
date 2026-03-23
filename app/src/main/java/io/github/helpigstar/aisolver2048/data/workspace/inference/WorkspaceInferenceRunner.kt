package io.github.helpigstar.aisolver2048.data.workspace.inference

interface WorkspaceInferenceRunner {
    suspend fun runInference(
        boardValues: List<Int>,
    ): WorkspaceInferenceResult
}

sealed interface WorkspaceInferenceResult {
    data class Success(
        val policyLogits: FloatArray,
    ) : WorkspaceInferenceResult

    data object InferenceFailed : WorkspaceInferenceResult

    data object Unavailable : WorkspaceInferenceResult
}
