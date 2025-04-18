package com.example.workouttracker.ui.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.HorizontalDivider
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorBorder
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Pager(vm: PagerViewModel = hiltViewModel()) {
    val selectedPage by vm.selectedPage.collectAsStateWithLifecycle()
    val pages by vm.pages.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        vm.changeSelection(pages[pagerState.currentPage])
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(selectedPage.title), color = ColorWhite)
            }
        }

        HorizontalDivider(color = ColorBorder, thickness = 2.dp)

        TabRow(
            containerColor = Color.Transparent,
            selectedTabIndex = selectedPage.index,
            indicator = { tabPositions: List<TabPosition> ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedPage.index]),
                    color = ColorAccent,
                    height = 1.dp
                )
            },
            divider = {}
        ) {
            pages.forEachIndexed { index, tab ->
                val isSelected = selectedPage.index == index

                Tab(
                    selected = selectedPage.index == index,
                    onClick = {
                        vm.changeSelection(pages[index])
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(selectedPage.index)
                        }
                    },
                    text = {
                        val color = if (isSelected) {
                            ColorAccent
                        } else {
                            ColorWhite
                        }

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