package com.chillyblaze.autonight.view.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillyblaze.autonight.R

@Composable
fun AboutCard() {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
    ) {
        Column(Modifier.padding(15.dp, 5.dp)) {
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = stringResource(id = R.string.about_content1),
                style = MaterialTheme.typography.titleMedium
            )
            LinkButton(
                uri = "https://github.com/topjohnwu/libsu",
                icon = R.drawable.ic_github,
                name = " topjohnwu/libsu"
            )
            Divider()
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = stringResource(id = R.string.about_content2),
                style = MaterialTheme.typography.titleMedium
            )
            LinkButton(
                uri = "https://t.me/ChillyBlaze",
                icon = R.drawable.ic_telegram,
                name = " @ChillyBlaze"
            )
        }
    }
}

@Composable
private fun LinkButton(uri: String, icon: Int, name: String) {
    val context = LocalContext.current
    TextButton(onClick = {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }) {
        Icon(painter = painterResource(id = icon), contentDescription = null)
        Text(text = name, style = MaterialTheme.typography.bodyMedium)
    }
}