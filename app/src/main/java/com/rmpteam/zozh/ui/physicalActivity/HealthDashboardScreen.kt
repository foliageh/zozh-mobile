package com.rmpteam.zozh.ui.physicalActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmpteam.zozh.data.physicalActivity.FakeActivityRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDashboardScreen() {
    val repository = remember { FakeActivityRepository() }

    val steps = remember { mutableStateOf(repository.getSteps()) }
    val calories = remember { mutableStateOf(repository.getCalories()) }
    val heartRate = remember { mutableStateOf(repository.getCurrentHeartRate()) }
    val bloodPressure = remember {
        mutableStateOf(
            repository.getHeartPressure().let { "${it.first}/${it.second}" }
        )
    }

    val workouts = remember { repository.getLastThreeActivities() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мои показатели") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StepsAndCaloriesCard(steps.value, calories.value)
            }

            item {
                LineChart(
                    data = listOf(70, 75, 80, 72, 68, 65, 70),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            item {
                HeartRateBloodPressureCard(heartRate.value, bloodPressure.value)
            }

            item {
                Text(
                    text = "Последние тренировки",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(workouts) { workout ->
                WorkoutItem(workout = workout)
            }
        }
    }
}

@Composable
fun StepsAndCaloriesCard(steps: Int, calories: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HealthMetric(
                value = "$steps",
                label = "Шаги"
            )
            HealthMetric(
                value = "$calories",
                label = "Ккал"
            )
        }
    }
}

@Composable
fun HealthMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LineChart(data: List<Int>, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 100
    val minValue = data.minOrNull() ?: 0
    val colorScheme = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val spacePerPoint = size.width / (data.size - 1)
        val heightRatio = size.height / (maxValue - minValue)

        // Draw grid
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f
        )

        // Draw line
        val points = data.mapIndexed { index, value ->
            Offset(
                x = index * spacePerPoint,
                y = size.height - (value - minValue) * heightRatio
            )
        }

        points.forEachIndexed { index, offset ->
            if (index < points.size - 1) {
                drawLine(
                    color = colorScheme.primary,
                    start = offset,
                    end = points[index + 1],
                    strokeWidth = 3f
                )
            }
        }
    }
}

@Composable
fun HeartRateBloodPressureCard(heartRate: Int, bloodPressure: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HealthMetric(
                value = "$heartRate",
                label = "Пульс"
            )
            HealthMetric(
                value = bloodPressure,
                label = "Давление"
            )
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.type,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = workout.duration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class Workout(
    val type: String,
    val duration: String,
    //val icon: Int
)

@Preview(showBackground = true)
@Composable
fun PreviewHealthDashboard() {
    MaterialTheme {
        HealthDashboardScreen()
    }
}