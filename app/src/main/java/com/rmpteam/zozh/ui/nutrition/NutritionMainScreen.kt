package com.rmpteam.zozh.ui.nutrition

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    val userPreferencesUiState by viewModel.userPreferencesUiState.collectAsStateWithLifecycle()
    Log.d("Calories", userPreferencesUiState.calories.toString())

    NutritionMainScreenContent(
        modifier = modifier.fillMaxSize(),
        date = uiState.date,
        mealList = uiState.mealList,
        onNutritionRecordClick = onNavigateToNutritionRecord
    )
}

@Composable
fun NutritionMainScreenContent(
    modifier: Modifier = Modifier,
    date: ZonedDateTime,
    mealList: List<MealRecord>,
    onNutritionRecordClick: (mealId: Long) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Дата: ${date.dateString()}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        NutritionList(
            mealList = mealList,
            onNutritionRecordClick = onNutritionRecordClick
        )
    }
}

@Composable
fun NutritionList(
    modifier: Modifier = Modifier,
    mealList: List<MealRecord>,
    onNutritionRecordClick: (mealId: Long) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(mealList, key = { it.id }) { meal ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                headlineContent = { Text(text = meal.dateTime.timeString()) },
                supportingContent = { Text(text = meal.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNutritionRecordClick(meal.id) },
            )
            HorizontalDivider()
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
            onNutritionRecordClick = {}
        )
    }
}