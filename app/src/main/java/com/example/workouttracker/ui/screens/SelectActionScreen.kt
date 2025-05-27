package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.components.reusable.ActionItem
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.SelectActionViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.workouttracker.ui.managers.LoadingManager

/**
 * Screen used to allow the user to select additional actions -
 * manage exercise/templates, finish workout / save workout as template and others
 */
@Composable
fun SelectActionScreen(vm: SelectActionViewModel = hiltViewModel()) {
    val initialized by vm.isInitialized.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    DisposableEffect (Unit) {
        onDispose {
            vm.resetData()
        }
    }

    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    LaunchedEffect(initialized) {
        if (initialized) {
            LoadingManager.hideLoading()
        } else {
            LoadingManager.showLoading()
        }
    }

    if (initialized) {
        val actions by vm.actions.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(actions) { item ->
                ActionItem(
                    imageId = item.imageId,
                    titleId = item.titleId,
                    onClick = {
                        scope.launch {
                            item.onClick()
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectActionScreenPreview() {
    WorkoutTrackerTheme {
        SelectActionScreen()
    }
}