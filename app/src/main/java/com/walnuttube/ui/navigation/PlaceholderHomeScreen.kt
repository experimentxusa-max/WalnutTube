package com.walnuttube.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaceholderHomeScreen(onNavigateToTest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "WalnutTube", fontSize = 32.sp)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(16.dp))
        Text(text = "Milestone 1 — Foundation", fontSize = 18.sp)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(32.dp))
        Button(onClick = onNavigateToTest) {
            Text(text = "Open Extraction Test", fontSize = 18.sp)
        }
    }
}