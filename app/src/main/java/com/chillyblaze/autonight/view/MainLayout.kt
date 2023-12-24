package com.chillyblaze.autonight.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.view.component.AboutCard
import com.chillyblaze.autonight.view.component.DashboardCard
import com.chillyblaze.autonight.view.component.EnvironmentCard
import com.chillyblaze.autonight.view.component.SettingCard
import com.chillyblaze.autonight.view.component.TipsCard
import com.chillyblaze.autonight.view.component.TopBar

@Composable
fun MainLayout() {
    AutoNightTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(topBar = { TopBar() }) {
                Column(modifier = Modifier.padding(it).verticalScroll(rememberScrollState())) {
                    EnvironmentCard()
                    DashboardCard(integerResource(id = R.integer.card_delay_1))
                    SettingCard(integerResource(id = R.integer.card_delay_2))
                    TipsCard(integerResource(id = R.integer.card_delay_3))
                    AboutCard()
                }
            }
        }
    }
}