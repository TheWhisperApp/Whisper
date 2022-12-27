package dev.whisper.voice.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import dev.whisper.voice.ui.theme.whispericonpack.`Tree-christmas`
import kotlin.collections.List as ____KtList

public object WhisperIconPack

private var __WhisperIcon: ____KtList<ImageVector>? = null

public val WhisperIconPack.WhisperIcon: ____KtList<ImageVector>
  get() {
    if (__WhisperIcon != null) {
      return __WhisperIcon!!
    }
    __WhisperIcon= listOf(`Tree-christmas`)
    return __WhisperIcon!!
  }
