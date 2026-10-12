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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.nur.aiagent.agent.TaskPlan
import io.nur.aiagent.agent.TaskPlanner
import io.nur.aiagent.memory.LocalMemoryStore
import io.nur.aiagent.memory.SavedPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var taskInput by remember { mutableStateOf("") }
    var taskPlan by remember { mutableStateOf<TaskPlan?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var memoryMessage by remember { mutableStateOf<String?>(null) }
    var savedPlans by remember { mutableStateOf<List<SavedPlan>>(emptyList()) }

    val appContext = LocalContext.current.applicationContext
    val memoryStore = remember { LocalMemoryStore(appContext) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(memoryStore) {
        savedPlans = withContext(Dispatchers.IO) { memoryStore.getSavedPlans() }
    }
    DisposableEffect(memoryStore) {
        onDispose { memoryStore.close() }
    }

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
                text = "আপনার ব্যক্তিগত AI সহকারী — ধাপে ধাপে তৈরি হচ্ছে।",
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
                    Text("বর্তমান অবস্থা", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("প্রাথমিক Android অ্যাপ চালু আছে। AI API এখনো সংযুক্ত নয়।")
                    Text("এই সংস্করণে অন্য অ্যাপ নিয়ন্ত্রণ বা সংবেদনশীল ফোন-অনুমতি নেই।")
                }
            }

            Text("আপনার কাজ লিখুন", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = taskInput,
                onValueChange = {
                    if (it.length <= TaskPlanner.MAX_REQUEST_LENGTH) {
                        taskInput = it
                        validationError = null
                    }
                },
                label = { Text("যেমন: আমার পড়াশোনার রুটিন তৈরি করো") },
                supportingText = { Text("${taskInput.length}/${TaskPlanner.MAX_REQUEST_LENGTH} অক্ষর") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    try {
                        taskPlan = TaskPlanner().createPlan(taskInput)
                        validationError = null
                        memoryMessage = null
                    } catch (error: IllegalArgumentException) {
                        taskPlan = null
                        validationError = error.message ?: "এই অনুরোধটি যাচাই করা যায়নি।"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("পরিকল্পনা তৈরি করুন")
            }

            validationError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            taskPlan?.let { plan ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("আপনার পরিকল্পনা", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(plan.summary)
                        if (plan.requiresApproval) {
                            Text(
                                "অনুমতি আবশ্যক",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            plan.approvalReason?.let { Text(it) }
                        } else {
                            Text("প্রাথমিক পর্যালোচনা", color = Emerald, fontWeight = FontWeight.Bold)
                        }
                        plan.steps.forEachIndexed { index, step ->
                            PlanStep(
                                number = (index + 1).toString(),
                                title = step.title,
                                detail = step.description
                            )
                        }
                        Button(
                            onClick = {
                                val planToSave = taskPlan
                                if (planToSave != null) {
                                    scope.launch {
                                        try {
                                            withContext(Dispatchers.IO) {
                                                memoryStore.savePlan(planToSave)
                                            }
                                            savedPlans = withContext(Dispatchers.IO) {
                                                memoryStore.getSavedPlans()
                                            }
                                            memoryMessage = "পরিকল্পনাটি এই ফোনে সংরক্ষণ করা হয়েছে।"
                                        } catch (_: Exception) {
                                            memoryMessage = "সংরক্ষণ করা যায়নি। আবার চেষ্টা করুন।"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("পরিকল্পনাটি ফোনের মেমোরিতে সংরক্ষণ করুন")
                        }
                        Text(
                            "সংরক্ষণে চাপ না দিলে কিছুই মেমোরিতে রাখা হবে না।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            memoryMessage?.let { Text(it, color = Emerald) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("স্থানীয় মেমোরি", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("এখানে সংরক্ষিত পরিকল্পনা শুধু এই অ্যাপের ব্যক্তিগত ডেটাবেসে থাকে; AI সার্ভারে পাঠানো হয় না।")
                    if (savedPlans.isEmpty()) {
                        Text("এখনো কোনো পরিকল্পনা সংরক্ষিত নেই।")
                    } else {
                        savedPlans.forEach { saved ->
                            SavedPlanRow(
                                savedPlan = saved,
                                onDelete = {
                                    scope.launch {
                                        val deleted = withContext(Dispatchers.IO) {
                                            memoryStore.deleteSavedPlan(saved.id)
                                        }
                                        savedPlans = withContext(Dispatchers.IO) {
                                            memoryStore.getSavedPlans()
                                        }
                                        memoryMessage = if (deleted) {
                                            "সংরক্ষিত পরিকল্পনাটি মুছে ফেলা হয়েছে।"
                                        } else {
                                            "পরিকল্পনাটি পাওয়া যায়নি; তালিকাটি আপডেট করা হয়েছে।"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "নিরাপত্তা প্রথমে: সংবেদনশীল কাজ বাস্তবায়নের আগে স্পষ্ট অনুমতি লাগবে।",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun SavedPlanRow(savedPlan: SavedPlan, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(savedPlan.request, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(savedPlan.summary, style = MaterialTheme.typography.bodySmall)
            Text(
                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(savedPlan.createdAtMillis)),
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
            TextButton(onClick = onDelete) {
                Text("মুছে ফেলুন")
            }
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
