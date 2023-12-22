package com.chillyblaze.autonight.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chillyblaze.autonight.view.component.DashboardCard
import com.chillyblaze.autonight.view.component.HintCard
import com.chillyblaze.autonight.view.component.SettingCard
import com.chillyblaze.autonight.view.component.TopBar

@Composable
fun MainLayout() {
    AutoNightTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TopBar()
                HintCard()
                DashboardCard()
                SettingCard()
            }
        }
    }
}