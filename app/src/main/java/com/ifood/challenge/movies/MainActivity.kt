package com.ifood.challenge.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ifood.challenge.movies.core.designsystem.theme.IfoodMoviesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            IfoodMoviesTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
