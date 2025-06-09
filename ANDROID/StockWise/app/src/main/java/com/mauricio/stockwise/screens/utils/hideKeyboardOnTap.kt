package com.mauricio.stockwise.ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.composed

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.hideKeyboardOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    this.pointerInteropFilter {
        focusManager.clearFocus()
        false
    }
}