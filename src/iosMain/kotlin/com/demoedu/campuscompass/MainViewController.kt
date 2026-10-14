package com.demoedu.campuscompass

import androidx.compose.ui.window.ComposeUIViewController
import com.demoedu.campuscompass.data.buildCampusDatabase
import com.demoedu.campuscompass.data.getCampusDatabaseBuilder

fun MainViewController() = ComposeUIViewController {
    App(buildCampusDatabase(getCampusDatabaseBuilder()))
}
