package com.example.workouttracker.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.viewmodel.PagerViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.viewmodel.Page

@Composable
fun Pager(vm: PagerViewModel = hiltViewModel()) {
    val selectedPage by vm.selectedPage.collectAsStateWithLifecycle()
    val pages by vm.pages.collectAsStateWithLifecycle()
    val selectedTabIndex = pages.indexOf(selectedPage).takeIf { it >= 0 } ?: 0

    LaunchedEffect(Unit) {
        PagerManager.events.collect { page ->
            vm.changeSelection(page)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PagesContent(
            modifier = Modifier.weight(1f),
            selectedPage = selectedPage,
            pages = pages,
            onSwipe = { vm.changeSelection(it) })

        HorizontalDivider(color = ColorBorder, thickness = 2.dp)

        TabRow(
            containerColor = Color.Transparent,
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions: List<TabPosition> ->
                if (selectedTabIndex < tabPositions.size) {
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = ColorAccent,
                        height = 1.dp
                    )
                }
            },
            divider = {}
        ) {
            pages.forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index

                Tab(
                    selected = isSelected,
                    onClick = { vm.changeSelection(pages[index]) },
                    text = {
                        val color = if (isSelected) ColorAccent else ColorWhite
                        Text(
                            text = stringResource(id = tab.title),
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = tab.icon),
                            contentDescription = null,
                            colorFilter = if (isSelected) null else ColorFilter.tint(ColorWhite)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Box with swipe behavior displaying the different pages from the PagerViewModel.
 * Changed from HorizontalPager to custom solution, because HorizontalPager requires
 * pager state which is harder sync with our implementation of being able to display
 * permanent and temporary panels and change the selected page with
 * swipe / choose from the tab row / programmatically
 * @param modifier the modifier applied to the Box
 * @param selectedPage the currently selected page
 * @param pages all pages
 * @param onSwipe callback to execute on swipe
 * */
@Composable
fun PagesContent(modifier: Modifier, selectedPage: Page, pages: List<Page>, onSwipe: (Page) -> Unit) {
    val swipeThreshold = 25f
    var previousIndex by remember { mutableIntStateOf(pages.indexOf(selectedPage)) }
    val currentIndex = pages.indexOf(selectedPage)

    LaunchedEffect(currentIndex) {
        previousIndex = currentIndex
    }

    Box(modifier = modifier.then(Modifier.pointerInput(pages, selectedPage) {
        detectHorizontalDragGestures { change, dragAmount ->
            if (dragAmount > swipeThreshold) {
                val newIndex = (pages.indexOf(selectedPage) - 1).coerceAtLeast(0)
                onSwipe(pages[newIndex])

            } else if (dragAmount < -swipeThreshold) {
                val newIndex = (pages.indexOf(selectedPage) + 1).coerceAtMost(pages.lastIndex)
                onSwipe(pages[newIndex])
            }
        }
    })) {
        AnimatedContent(
            targetState = selectedPage,
            transitionSpec = {
                if (currentIndex >= previousIndex) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "AnimatedPageContent"
        ) { page ->
            page.content()
        }
    }
}