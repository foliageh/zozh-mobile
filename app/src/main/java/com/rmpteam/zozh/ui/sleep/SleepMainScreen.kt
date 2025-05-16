package com.rmpteam.zozh.ui.sleep

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepQuality
import com.rmpteam.zozh.di.AppViewModelProvider
import com.rmpteam.zozh.util.DateTimeUtil.dateString
import com.rmpteam.zozh.util.DateTimeUtil.timeString
import java.time.Duration

@Composable
fun SleepMainScreen(
    modifier: Modifier = Modifier,
    onSleepItemClick: (Sleep) -> Unit = {}
) {
    val viewModel = viewModel<SleepMainViewModel>(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isCurrentWeek = remember(uiState.date) { viewModel.isCurrentWeek(uiState.date) }

    SleepScreenContent(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        isCurrentWeek = isCurrentWeek, 
        onPreviousDate = { viewModel.updateDate(uiState.date.minusDays(7)) },
        onNextDate = { viewModel.updateDate(uiState.date.plusDays(7)) },
        onStartTracking = { viewModel.startTrackingSleep() },
        onStopTracking = { viewModel.stopTrackingSleep() },
        onSleepItemClick = onSleepItemClick
    )
}

@Composable
fun SleepSummaryCard(
    sleepList: List<Sleep>,
    modifier: Modifier = Modifier
) {
    val avgDuration = remember(sleepList) {
        if (sleepList.isEmpty()) 0L
        else {
            val totalMinutes = sleepList.sumOf {
                val duration = Duration.between(it.startTime, it.endTime)
                if (duration.isNegative) 0L else duration.toMinutes()
            }
            totalMinutes / sleepList.size
        }
    }

    val qualityCount = remember(sleepList) {
        sleepList.groupingBy { it.quality }.eachCount()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Среднее время сна",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${avgDuration / 60} ч ${avgDuration % 60} мин",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SleepQuality.entries.forEach { quality ->
                    val count = qualityCount[quality] ?: 0
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when(quality) {
                                SleepQuality.POOR -> "Плохой"
                                SleepQuality.FAIR -> "Средний"
                                SleepQuality.GOOD -> "Хороший"
                                SleepQuality.EXCELLENT -> "Отличный"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleepQualityChart(
    sleepList: List<Sleep>,
    modifier: Modifier = Modifier
) {
    val qualityCount = remember(sleepList) {
        sleepList.groupingBy { it.quality }.eachCount()
    }

    val total = remember(sleepList) {
        sleepList.size.toFloat().coerceAtLeast(1f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Качество сна",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    SleepQuality.entries.forEach { quality ->
                        val count = qualityCount[quality] ?: 0
                        if (count > 0) {
                            val sweepAngle = 360f * (count / total)
                            val color = when(quality) {
                                SleepQuality.POOR -> Color.Red
                                SleepQuality.FAIR -> Color(0xFFFFA000)
                                SleepQuality.GOOD -> Color.Green
                                SleepQuality.EXCELLENT -> Color.Blue
                            }

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 30f, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                if (sleepList.isNotEmpty()) {
                    val mostCommonQuality = qualityCount.maxByOrNull { it.value }?.key ?: SleepQuality.GOOD
                    Text(
                        text = when(mostCommonQuality) {
                            SleepQuality.POOR -> "В основном\nплохой сон"
                            SleepQuality.FAIR -> "В основном\nсредний сон"
                            SleepQuality.GOOD -> "В основном\nхороший сон"
                            SleepQuality.EXCELLENT -> "В основном\nотличный сон"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SleepQuality.entries.forEach { quality ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = when(quality) {
                                    SleepQuality.POOR -> Color.Red
                                    SleepQuality.FAIR -> Color(0xFFFFA000)
                                    SleepQuality.GOOD -> Color.Green
                                    SleepQuality.EXCELLENT -> Color.Blue
                                }
                            ) {}
                        }
                        Text(
                            text = when(quality) {
                                SleepQuality.POOR -> "Плохой"
                                SleepQuality.FAIR -> "Средний"
                                SleepQuality.GOOD -> "Хороший"
                                SleepQuality.EXCELLENT -> "Отличный"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleepScreenContent(
    modifier: Modifier = Modifier,
    uiState: SleepMainUiState,
    isCurrentWeek: Boolean, 
    onPreviousDate: () -> Unit = {},
    onNextDate: () -> Unit = {},
    onStartTracking: () -> Unit = {},
    onStopTracking: () -> Unit = {},
    onSleepItemClick: (Sleep) -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDate) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущая неделя"
                )
            }

            Text(
                text = "Данные за неделю до ${uiState.date.dateString()}",
                style = MaterialTheme.typography.titleMedium
            )

            if (!isCurrentWeek) {
                IconButton(onClick = onNextDate) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Следующая неделя"
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        if (isCurrentWeek) {
            Button(
                onClick = if (uiState.isTrackingSleep) onStopTracking else onStartTracking,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isTrackingSleep) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (uiState.isTrackingSleep) "Остановить сон" else "Начать сон")
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.sleepList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных о сне за этот период",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            SleepSummaryCard(sleepList = uiState.sleepList)
            SleepQualityChart(sleepList = uiState.sleepList)
            Text(
                text = "История сна",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.sleepList) { sleep ->
                    SleepItem(
                        sleep = sleep,
                        onClick = { onSleepItemClick(sleep) }
                    )
                }
            }
        }
    }
}

@Composable
fun SleepItem(
    sleep: Sleep,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val durationMinutes = remember(sleep) {
        val duration = Duration.between(sleep.startTime, sleep.endTime)
        if (duration.isNegative) Duration.ZERO.toMinutes() else duration.toMinutes()
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sleep.startTime.dateString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${sleep.startTime.timeString()} - ${sleep.endTime.timeString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${durationMinutes / 60} ч ${durationMinutes % 60} мин",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when(sleep.quality) {
                        SleepQuality.POOR -> "Плохой"
                        SleepQuality.FAIR -> "Средний"
                        SleepQuality.GOOD -> "Хороший"
                        SleepQuality.EXCELLENT -> "Отличный"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when(sleep.quality) {
                        SleepQuality.POOR -> Color.Red
                        SleepQuality.FAIR -> Color(0xFFFFA000)
                        SleepQuality.GOOD -> Color.Green
                        SleepQuality.EXCELLENT -> Color.Blue
                    }
                )
            }
        }
    }
}