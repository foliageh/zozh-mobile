package com.rmpteam.zozh.ui.physicalActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmpteam.zozh.data.physicalActivity.FakeActivityRepository

data class WorkoutRecord(
    val id: Int = 0,
    val type: String = "",
    val duration: String = ""
)
data class WorkoutUiState(
    val workout: WorkoutRecord = WorkoutRecord(),
    val isWorkoutValid: Boolean = false,
    val isNewWorkout: Boolean = true
)

@Composable
fun WorkoutInputForm(workout: WorkoutRecord, onValueChange: (WorkoutRecord) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = workout.type,
            onValueChange = { onValueChange(workout.copy(type = it)) },
            label = { Text("Тип тренировки") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, // Разрешаем текст
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = workout.duration,
            onValueChange = { onValueChange(workout.copy(duration = it)) },
            label = { Text("Продолжительность (мин)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number // Только цифры
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun WorkoutRecordScreenContent(
    modifier: Modifier = Modifier,
    workoutUiState: WorkoutUiState,
    onValueChange: (WorkoutRecord) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(modifier = modifier.padding(16.dp)) {
        WorkoutInputForm(
            workout = workoutUiState.workout,
            onValueChange = onValueChange
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSaveClick,
            enabled = workoutUiState.isWorkoutValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (workoutUiState.isNewWorkout) "Добавить" else "Сохранить")
        }
        if (!workoutUiState.isNewWorkout) {
            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Удалить")
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Обработка клика */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.type,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${workout.duration} минут",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDashboardScreen() {
    val repository = remember { FakeActivityRepository() }
    var showAddDialog by remember { mutableStateOf(false) }
    val workoutState = remember { mutableStateOf(WorkoutUiState()) }

    // Восстановленные показатели
    val steps = remember { mutableStateOf(repository.getSteps()) }
    val calories = remember { mutableStateOf(repository.getCalories()) }
    val heartRate = remember { mutableStateOf(repository.getCurrentHeartRate()) }
    val bloodPressure = remember {
        mutableStateOf(repository.getHeartPressure().let { "${it.first}/${it.second}" })
    }

    val workouts = remember { repository.getLastZeroActivities().toMutableStateList() }

    Scaffold(
        bottomBar = {
            CenterAlignedTopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Добавить тренировку")
                    }
                }
            )
        },

        floatingActionButton = {
            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Новая тренировка") },
                    text = {
                        WorkoutRecordScreenContent(
                            workoutUiState = workoutState.value,
                            onValueChange = { newRecord ->
                                workoutState.value = workoutState.value.copy(
                                    workout = newRecord,
                                    isWorkoutValid = newRecord.type.isNotBlank() &&
                                            newRecord.duration.isNotBlank()
                                )
                            },
                            onSaveClick = {
                                workouts.add(0, workoutState.value.workout.toWorkout())
                                showAddDialog = false
                                workoutState.value = WorkoutUiState()
                            }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StepsAndCaloriesCard(steps.value, calories.value) }
            item {
                LineChart(
                    data = listOf(70, 75, 80, 72, 68, 65, 70),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            item { HeartRateBloodPressureCard(heartRate.value, bloodPressure.value) }

            item {
                Text(
                    "Последние тренировки",
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

        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f
        )

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

private fun WorkoutRecord.toWorkout() = Workout(
    type = this.type,
    duration = this.duration
)
data class Workout(
    val type: String,
    val duration: String,
)

@Preview(showBackground = true)
@Composable
fun PreviewHealthDashboard() {
    MaterialTheme {
        HealthDashboardScreen()
    }
}