package com.example.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.example.a02_todoapp.Category

@Stable
data class CatColors(val container: Color, val onContainer: Color)

@Composable
fun categoryColors(category: Category): CatColors {
    val cs = MaterialTheme.colorScheme
    return when (category) {
        Category.PERSONAL -> CatColors(cs.primaryContainer,   cs.onPrimaryContainer)
        Category.WORK     -> CatColors(cs.secondaryContainer, cs.onSecondaryContainer)
        Category.PHYSICAL -> CatColors(cs.tertiaryContainer,  cs.onTertiaryContainer)
        Category.STUDY    -> CatColors(cs.errorContainer,     cs.onErrorContainer)     // markant
        Category.OTHER    -> CatColors(cs.surfaceVariant,     cs.onSurfaceVariant)
    }
}
