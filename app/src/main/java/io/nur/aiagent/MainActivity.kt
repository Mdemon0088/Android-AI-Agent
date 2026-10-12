package io.nur.aiagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Emerald = Color(0xFF087F5B)
private val Gold = Color(0xFFC69C38)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Emerald,
                    secondary = Gold,
                    background = Color(0xFFF7F8F5),
                    surface = Color.White
                )
            ) {
                NurAiHome()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun NurAiHome() {
    var planVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "NŪR AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Emerald
            )
            Text(
                text = "Your personal AI agent — built step by step.",
                style = MaterialTheme.typography.titleMedium
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Starter shell is running. AI/API connection is not configured yet.")
                    Text("No device-control permissions are requested in this version.")
                }
            }
            Text("First milestone", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Create a task plan, review it, then add a secure AI connection.")
            Button(
                onClick = { planVisible = !planVisible },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (planVisible) "Hide sample plan" else "Show sample plan")
            }
            if (planVisible) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PlanStep("1", "Understand", "Clarify the task and its constraints.")
                        PlanStep("2", "Plan", "Break the task into small, reviewable steps.")
                        PlanStep("3", "Request approval", "Ask before any sensitive action.")
                        PlanStep("4", "Verify", "Check the outcome and report what happened.")
                        PlanStep("5", "Remember", "Save only useful, user-approved notes.")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Safety first: this starter does not control other apps, read private data, or run tasks in the background.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun PlanStep(number: String, title: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(number, color = Emerald, fontWeight = FontWeight.Bold)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
