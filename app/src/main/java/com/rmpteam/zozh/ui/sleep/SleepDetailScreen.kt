package com.rmpteam.zozh.ui.sleep

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.sleep.SleepPhaseType
import com.rmpteam.zozh.data.sleep.SleepQuality
import com.rmpteam.zozh.data.sleep.SleepWithPhases
import com.rmpteam.zozh.di.AppViewModelProvider
import com.rmpteam.zozh.util.DateTimeUtil.dateString
import com.rmpteam.zozh.util.DateTimeUtil.timeString
import java.time.Duration

@Composable
fun SleepDetailScreen(
    sleepId: Long,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel = viewModel<SleepDetailViewModel>(factory = AppViewModelProvider.Factory)
    val uiState = viewModel.uiState

    LaunchedEffect(sleepId) {
        viewModel.loadSleepDetails(sleepId)
    }

    SleepDetailContent(
        sleepWithPhases = uiState.sleepWithPhases,
        isLoading = uiState.isLoading,
        modifier = modifier
    )
}

@Composable
fun SleepDetailContent(
    sleepWithPhases: SleepWithPhases?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (sleepWithPhases == null) {
            Text(
                text = "Данные о сне не найдены",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val sleep = sleepWithPhases.sleep

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Сон ${sleep.startTime.dateString()}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row {
                            Text(
                                text = "Начало: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = sleep.startTime.timeString())
                        }

                        Row {
                            Text(
                                text = "Окончание: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = sleep.endTime.timeString())
                        }

                        val duration = Duration.between(sleep.startTime, sleep.endTime)
                        val hours = duration.toHours()
                        val minutes = duration.toMinutes() % 60

                        Row {
                            Text(
                                text = "Длительность: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "$hours ч $minutes мин")
                        }

                        Row {
                            Text(
                                text = "Качество: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when(sleep.quality) {
                                    SleepQuality.POOR -> "Плохой"
                                    SleepQuality.FAIR -> "Средний"
                                    SleepQuality.GOOD -> "Хороший"
                                    SleepQuality.EXCELLENT -> "Отличный"
                                }
                            )
                        }

                        if (sleep.notes.isNotBlank()) {
                            Row {
                                Text(
                                    text = "Заметки: ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = sleep.notes)
                            }
                        }
                    }
                }

                if (sleepWithPhases.phases.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Фазы сна",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val width = size.width
                                    val height = size.height

                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, height - 20),
                                        end = Offset(width, height - 20),
                                        strokeWidth = 2f
                                    )

                                    val startTime = sleep.startTime
                                    val endTime = sleep.endTime
                                    val totalDuration = Duration.between(startTime, endTime).toMillis().toFloat()

                                    sleepWithPhases.phases.forEach { phase ->
                                        val phaseStart = Duration.between(startTime, phase.startTime).toMillis() / totalDuration * width
                                        val phaseWidth = Duration.between(phase.startTime, phase.endTime).toMillis() / totalDuration * width

                                        val phaseColor = when(phase.type) {
                                            SleepPhaseType.LIGHT -> Color.LightGray
                                            SleepPhaseType.DEEP -> Color.DarkGray
                                            SleepPhaseType.REM -> Color.Blue
                                        }

                                        val yPos = when(phase.type) {
                                            SleepPhaseType.LIGHT -> height * 0.4f
                                            SleepPhaseType.DEEP -> height * 0.7f
                                            SleepPhaseType.REM -> height * 0.2f
                                        }

                                        drawLine(
                                            color = phaseColor,
                                            start = Offset(phaseStart, yPos),
                                            end = Offset(phaseStart + phaseWidth, yPos),
                                            strokeWidth = 20f
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PhaseColorIndicator(color = Color.LightGray, label = "Легкий сон")
                                PhaseColorIndicator(color = Color.DarkGray, label = "Глубокий сон")
                                PhaseColorIndicator(color = Color.Blue, label = "REM-сон")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val phaseStats = remember(sleepWithPhases) {
                                val lightTime = sleepWithPhases.phases
                                    .filter { it.type == SleepPhaseType.LIGHT }
                                    .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
                                val deepTime = sleepWithPhases.phases
                                    .filter { it.type == SleepPhaseType.DEEP }
                                    .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
                                val remTime = sleepWithPhases.phases
                                    .filter { it.type == SleepPhaseType.REM }
                                    .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }

                                Triple(lightTime, deepTime, remTime)
                            }

                            Text(
                                text = "Легкий сон: ${phaseStats.first / 60} ч ${phaseStats.first % 60} мин",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Глубокий сон: ${phaseStats.second / 60} ч ${phaseStats.second % 60} мин",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "REM-сон: ${phaseStats.third / 60} ч ${phaseStats.third % 60} мин",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Рекомендации",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val recommendations = remember(sleep) {
                            val totalDuration = Duration.between(sleep.startTime, sleep.endTime).toHours()
                            val recommendations = mutableListOf<String>()

                            if (totalDuration < 7) {
                                recommendations.add("Вы спите меньше рекомендуемых 7-9 часов. Постарайтесь увеличить время сна.")
                            }

                            if (sleep.quality == SleepQuality.POOR || sleep.quality == SleepQuality.FAIR) {
                                recommendations.add("Качество сна недостаточно высокое. Попробуйте улучшить условия для сна: проветривайте комнату, уменьшите освещение и шум.")
                            }

                            if (sleep.startTime.hour in 0..5) {
                                recommendations.add("Вы ложитесь спать слишком поздно. Попробуйте лечь раньше для более здорового режима сна.")
                            }

                            if (recommendations.isEmpty()) {
                                recommendations.add("У вас хороший сон! Продолжайте соблюдать правильный режим сна.")
                            }

                            recommendations
                        }

                        recommendations.forEach { recommendation ->
                            Text(
                                text = "• $recommendation",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseColorIndicator(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            color = color,
            shape = MaterialTheme.shapes.small
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}