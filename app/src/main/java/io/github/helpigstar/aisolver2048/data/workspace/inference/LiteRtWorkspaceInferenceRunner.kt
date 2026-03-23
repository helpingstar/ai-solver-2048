package io.github.helpigstar.aisolver2048.data.workspace.inference

import android.app.Application
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.TensorBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.log2

private const val BOARD_CELL_COUNT: Int = 16
private const val MODEL_BOARD_GOAL: Float = 17f
private const val MODEL_ASSET_NAME: String = "game2048_policy_value_float32.tflite"
private const val MODEL_INPUT_NAME: String = "args_0"
private const val MODEL_POLICY_OUTPUT_NAME: String = "output_0"
private const val MODEL_VALUE_OUTPUT_NAME: String = "output_1"
private const val EXPECTED_POLICY_LOGIT_COUNT: Int = 4
private const val EXPECTED_VALUE_COUNT: Int = 1

class LiteRtWorkspaceInferenceRunner(
    private val application: Application,
) : WorkspaceInferenceRunner {
    @Volatile
    private var compiledModel: CompiledModel? = null

    @Volatile
    private var isModelUnavailable: Boolean = false

    private val modelLock = Any()

    override suspend fun runInference(
        boardValues: List<Int>,
    ): WorkspaceInferenceResult =
        withContext(Dispatchers.Default) {
            val model = getOrCreateCompiledModel() ?: return@withContext WorkspaceInferenceResult.Unavailable
            val inputBuffer = try {
                model.createInputBuffer(MODEL_INPUT_NAME)
            } catch (exception: Exception) {
                Timber.e(exception, "Failed to create LiteRT input buffer.")
                return@withContext WorkspaceInferenceResult.InferenceFailed
            }
            val policyOutputBuffer = try {
                model.createOutputBuffer(MODEL_POLICY_OUTPUT_NAME)
            } catch (exception: Exception) {
                inputBuffer.close()
                Timber.e(exception, "Failed to create LiteRT policy output buffer.")
                return@withContext WorkspaceInferenceResult.InferenceFailed
            }
            val valueOutputBuffer = try {
                model.createOutputBuffer(MODEL_VALUE_OUTPUT_NAME)
            } catch (exception: Exception) {
                closeQuietly(policyOutputBuffer, inputBuffer)
                Timber.e(exception, "Failed to create LiteRT value output buffer.")
                return@withContext WorkspaceInferenceResult.InferenceFailed
            }

            try {
                inputBuffer.writeFloat(
                    buildNormalizedInput(
                        boardValues = boardValues,
                    ),
                )
                model.run(
                    inputs = mapOf(MODEL_INPUT_NAME to inputBuffer),
                    outputs = mapOf(
                        MODEL_POLICY_OUTPUT_NAME to policyOutputBuffer,
                        MODEL_VALUE_OUTPUT_NAME to valueOutputBuffer,
                    ),
                )

                val policyLogits = policyOutputBuffer.readFloat()
                val valueOutput = valueOutputBuffer.readFloat()
                if (
                    policyLogits.size != EXPECTED_POLICY_LOGIT_COUNT ||
                    valueOutput.size != EXPECTED_VALUE_COUNT
                ) {
                    Timber.e(
                        "Unexpected LiteRT output shape. policy=%d value=%d",
                        policyLogits.size,
                        valueOutput.size,
                    )
                    WorkspaceInferenceResult.InferenceFailed
                } else {
                    WorkspaceInferenceResult.Success(
                        policyLogits = policyLogits,
                    )
                }
            } catch (exception: Exception) {
                Timber.e(exception, "LiteRT inference failed.")
                WorkspaceInferenceResult.InferenceFailed
            } finally {
                closeQuietly(valueOutputBuffer, policyOutputBuffer, inputBuffer)
            }
        }

    private fun getOrCreateCompiledModel(): CompiledModel? {
        if (isModelUnavailable) return null

        compiledModel?.let { model ->
            return model
        }

        synchronized(modelLock) {
            compiledModel?.let { model ->
                return model
            }

            return try {
                CompiledModel.create(
                    application.assets,
                    MODEL_ASSET_NAME,
                    CompiledModel.Options(Accelerator.CPU),
                ).also { model ->
                    compiledModel = model
                }
            } catch (exception: Exception) {
                isModelUnavailable = true
                Timber.e(exception, "Failed to load LiteRT model from assets.")
                null
            }
        }
    }

    private fun buildNormalizedInput(
        boardValues: List<Int>,
    ): FloatArray {
        require(boardValues.size == BOARD_CELL_COUNT) {
            "Workspace board must contain exactly 16 cells"
        }

        return FloatArray(size = BOARD_CELL_COUNT) { index ->
            normalizeTileValue(
                tileValue = boardValues[index],
            )
        }
    }

    private fun normalizeTileValue(tileValue: Int): Float =
        if (tileValue == 0) {
            0f
        } else {
            log2(tileValue.toFloat()) / MODEL_BOARD_GOAL
        }

    private fun closeQuietly(vararg buffers: TensorBuffer) {
        buffers.forEach { buffer ->
            try {
                buffer.close()
            } catch (_: Exception) {
                // Ignore close failures from temporary buffers.
            }
        }
    }
}
