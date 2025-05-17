package com.example.workouttracker.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.reusable.AppBackground
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorDrawer
import com.example.workouttracker.ui.theme.ColorGrey
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.LabelNavItem
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.DrawerViewModel
import kotlinx.coroutines.launch

data class NavItemData (
    val text: String,
    val icon: Painter,
    val onClick: () -> Unit
)

/** The drawer content */
@Composable
fun DrawerContent(
        email: String,
        fullName: String,
        profileImage: String,
        drawerState: DrawerState,
        vm: DrawerViewModel = hiltViewModel()
) {
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }
    val scope = rememberCoroutineScope()

    // Close the drawer on back button click instead of exiting the app
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalDrawerSheet(
        modifier = Modifier.padding(end = PaddingLarge * 2),
        drawerContainerColor = ColorDrawer
    ) {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingSmall)
            ) {
                DrawerHeader(
                    email = email,
                    fullName = fullName,
                    profileImage = profileImage
                )

                HorizontalDivider(
                    modifier = Modifier.padding(bottom = PaddingSmall),
                    color = ColorBorder,
                    thickness = 2.dp
                )

                getNavItems(
                    onLogout = { vm.logout() },
                    onEditProfile = { vm.showEditProfile() },
                    onChangeDefaultValues = { vm.showChangeDefaultValues() },
                    onChangePassword = { vm.showChangePassword() }
                ).forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Label(text = item.text, style = LabelNavItem)
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            // On click, indicate that the action was selected,
                            // close the drawer, then reset the selected index,
                            // and execute the on click event
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                                selectedItemIndex = -1
                                item.onClick()
                            }
                        },
                        icon = { Image(
                            painter = item.icon,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Action icon"
                        )},
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = ColorGrey
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(
        email: String,
        fullName: String,
        profileImage: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = PaddingSmall)
    ) {
        if (profileImage.isEmpty()) {
            Image(
                imageVector = Icons.Default.Person,
                colorFilter = ColorFilter.tint(color = ColorWhite),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(2.dp, ColorBorder, CircleShape)
                    .padding(PaddingSmall/2),
                contentDescription = "Profile image",
            )
        } else {
            val profileImagePainter = remember(profileImage) {
                val bitmap = Utils.convertStringToBitmap(profileImage)
                BitmapPainter(bitmap.asImageBitmap())
            }

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(2.dp, ColorBorder, CircleShape),
                painter = profileImagePainter,
                contentDescription = "Profile image"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = PaddingSmall),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Label(
                text = email,
                style = MaterialTheme.typography.labelLarge
            )
            Label(
                text = fullName,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun getNavItems(onLogout: () -> Unit,
                onEditProfile: () -> Unit,
                onChangeDefaultValues: () -> Unit,
                onChangePassword: () -> Unit): List<NavItemData> {
    return listOf(
        NavItemData(
            text = stringResource(id = R.string.exercise_default_values),
            icon = painterResource(id = R.drawable.icon_edit_exercise_def_vals),
            onClick = { onChangeDefaultValues() }
        ),
        NavItemData(
            text = stringResource(id = R.string.edit_profile),
            icon = painterResource(id = R.drawable.icon_edit_profile),
            onClick = { onEditProfile() }
        ),
        NavItemData(
            text = stringResource(id = R.string.change_password),
            icon = painterResource(id = R.drawable.icon_change_password),
            onClick = { onChangePassword() }
        ),
        NavItemData(
            text = stringResource(id = R.string.logout),
            icon = painterResource(id = R.drawable.icon_log_out),
            onClick = { onLogout() }
        ),
    )
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DrawerContentPreview() {
    WorkoutTrackerTheme {
        DrawerContent(
            email = "test@abv.bg",
            fullName = "Test user",
            profileImage = "",
            rememberDrawerState(DrawerValue.Closed)
        )
    }
}