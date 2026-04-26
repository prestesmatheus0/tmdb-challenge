package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ifood.challenge.movies.core.designsystem.theme.spacing

data class MovieFilterChip<T>(
    val key: T,
    val label: String,
)

@Composable
fun <T> FilterChipRow(
    chips: List<MovieFilterChip<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.testTag(FilterChipRowTestTags.root),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
    ) {
        items(chips, key = { it.label }) { chip ->
            val isSelected = chip.key == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(chip.key) },
                label = { Text(chip.label) },
                leadingIcon =
                    if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    },
                modifier = Modifier.testTag(FilterChipRowTestTags.chip(chip.label)),
            )
        }
    }
}

object FilterChipRowTestTags {
    const val root = "filter_chip_row"

    fun chip(label: String) = "filter_chip_$label"
}
