// app/src/main/java/com/rmpteam/zozh/ui/sleep/SleepScreen.kt
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<SleepViewModel>(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SleepScreenContent(
        modifier = modifier.fillMaxSize(),
        date = uiState.date,
        sleepList = uiState.sleepList,
        isLoading = uiState.isLoading,
        onPreviousDate = { viewModel.updateDate(uiState.date.minusDays(7)) },
        onNextDate = { viewModel.updateDate(uiState.date.plusDays(7)) }
    )
}

@Composable
fun SleepScreenContent(
    modifier: Modifier = Modifier,
    date: ZonedDateTime,
    sleepList: List<Sleep>,
    isLoading: Boolean,
    onPreviousDate: () -> Unit = {},
    onNextDate: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDate) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущая неделя")
            }
            Text(
                text = "Данные за неделю до ${date.dateString()}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onNextDate) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующая неделя")
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (sleepList.isEmpty()) {
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
            // Sleep Summary Card
            SleepSummaryCard(sleepList = sleepList)
            
            // Sleep Quality Chart
            SleepQualityChart(sleepList = sleepList)
            
            // Sleep List
            Text(
                text = "История сна",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sleepList) { sleep ->
                    SleepItem(sleep = sleep)
                }
            }
        }
    }
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

    val hours = avgDuration / 60
    val minutes = avgDuration % 60
    
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
                text = "$hours ч $minutes мин",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val qualityCount = remember(sleepList) {
                sleepList.groupingBy { it.quality }.eachCount()
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SleepQuality.values().forEach { quality ->
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
    
    val total = sleepList.size.toFloat()
    
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
                    val strokeWidth = 30f
                    val radius = (size.minDimension - strokeWidth) / 2
                    var startAngle = -90f
                    
                    SleepQuality.values().forEachIndexed { index, quality ->
                        val count = qualityCount[quality] ?: 0
                        if (count > 0) {
                            val sweepAngle = 360f * (count / total)
                            val color = when(quality) {
                                SleepQuality.POOR -> Color.Red
                                SleepQuality.FAIR -> Color.Yellow
                                SleepQuality.GOOD -> Color.Green
                                SleepQuality.EXCELLENT -> Color.Blue
                            }
                            
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
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
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SleepQuality.values().forEach { quality ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.Center)
                                    .padding(2.dp)
                            ) {
                                val color = when(quality) {
                                    SleepQuality.POOR -> Color.Red
                                    SleepQuality.FAIR -> Color.Yellow
                                    SleepQuality.GOOD -> Color.Green
                                    SleepQuality.EXCELLENT -> Color.Blue
                                }
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = color
                                ) {}
                            }
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
fun SleepItem(
    sleep: Sleep,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    val durationMinutes = remember(sleep) {
        val duration = Duration.between(sleep.startTime, sleep.endTime)
        if (duration.isNegative) Duration.ZERO.toMinutes() else duration.toMinutes()
    }

    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
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
                    text = sleep.startTime.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${sleep.startTime.format(timeFormatter)} - ${sleep.endTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$hours ч $minutes мин",
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
                        SleepQuality.FAIR -> Color.Yellow
                        SleepQuality.GOOD -> Color.Green
                        SleepQuality.EXCELLENT -> Color.Blue
                    }
                )
            }
        }
    }
}