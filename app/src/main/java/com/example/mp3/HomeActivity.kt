package com.example.mp3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mp3.ui.theme.MP3Theme


class HomeActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val composableKey = intent.getStringExtra("composable_key")
            when (composableKey) {
                "specific_composable" -> HomeScreen()
                else -> HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "hello",
        modifier = Modifier.fillMaxWidth()
            )
}

@Preview(showBackground = true)
@Composable
fun test() {
    MP3Theme {
        HomeScreen()
    }
}
