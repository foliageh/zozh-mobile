package com.rmpteam.zozh.ui.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.nutrition.FakeMealDatasource
import com.rmpteam.zozh.di.AppViewModelProvider
import com.rmpteam.zozh.ui.theme.ZOZHTheme
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.DateTimeUtil.dateString
import com.rmpteam.zozh.util.DateTimeUtil.startOfDay
import com.rmpteam.zozh.util.DateTimeUtil.timeString
import java.time.ZonedDateTime

@Composable
fun NutritionMainScreen(
    modifier: Modifier = Modifier,
    onNavigateToNutritionRecord: (mealId: Long) -> Unit
) {
    val viewModel = viewModel<NutritionMainViewModel>(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

//    val userPreferencesUiState by viewModel.userProfileState.collectAsStateWithLifecycle()

    NutritionMainScreenContent(
        modifier = modifier.fillMaxSize(),
        date = uiState.date,
        mealList = uiState.mealList,
        goalCalories =
//            userPreferencesUiState.userProfile.calories ?:
            2100,
        onNutritionRecordClick = onNavigateToNutritionRecord,
        onPreviousDate = { viewModel.updateDate(uiState.date.minusDays(1)) },
        onNextDate = { viewModel.updateDate(uiState.date.plusDays(1)) }
    )
}

@Composable
fun NutritionMainScreenContent(
    modifier: Modifier = Modifier,
    date: ZonedDateTime,
    mealList: List<MealRecord>,
    goalCalories: Int?,
    onNutritionRecordClick: (mealId: Long) -> Unit,
    onPreviousDate: () -> Unit = {},
    onNextDate: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Date selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDate) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий день")
            }
            Text(text = date.dateString(), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onNextDate) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующий день")
            }
        }

        // Summary of macros
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val (protein, fat, carbs) = mealList.fold(Triple(0, 0, 0)) { acc, m ->
                    Triple(acc.first + m.protein, acc.second + m.fat, acc.third + m.carbs)
                }
                val totalCalories = protein * 4 + fat * 9 + carbs * 4

                Spacer(modifier = Modifier.weight(1f))
                listOf("Б", "Ж", "У", "Калории").forEach { label ->
                    Column(
                        modifier = Modifier.weight(if (label == "Калории") 2f else 1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (label) {
                                "Б" -> protein.toString()
                                "Ж" -> fat.toString()
                                "У" -> carbs.toString()
                                else -> "$totalCalories${if (goalCalories != null) " (${totalCalories*100/goalCalories}%)" else ""}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (label == "Калории" && goalCalories != null && totalCalories > goalCalories) MaterialTheme.colorScheme.error else Color.Unspecified
                        )
                    }
                }
            }
        }

        // Meal list
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mealList) { meal ->
                MealItem(
                    meal = meal,
                    goalCalories = goalCalories,
                    onClick = { onNutritionRecordClick(meal.id) }
                )
            }
        }
    }
}

@Composable
fun MealItem(
    meal: MealRecord,
    goalCalories: Int?,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
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
                modifier = Modifier.weight(4.5f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = meal.dateTime.timeString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = meal.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(2f))
                    Text(
                        text = meal.protein.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = meal.fat.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = meal.carbs.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val totalCalories = meal.protein * 4 + meal.fat * 9 + meal.carbs * 4
                Text(
                    text = "$totalCalories ккал",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
                Text(
                    text = if (goalCalories != null) "${totalCalories*100/goalCalories}%" else "",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionMainScreenPreview() {
    ZOZHTheme {
        NutritionMainScreenContent(
            modifier = Modifier.fillMaxSize(),
            date = DateTimeUtil.now().startOfDay(),
            mealList = FakeMealDatasource.mealList.map { it.toMealRecord() },
            goalCalories = 2500,
            onNutritionRecordClick = {  },
            onPreviousDate = {  },
            onNextDate = {  }
        )
    }
}