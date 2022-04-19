package com.univ.mytodo.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun MainScaffold(
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    fab: @Composable() () -> Unit
) {
    Scaffold(
        topBar = topBar,
        content = content,
        floatingActionButton = fab
    )
}