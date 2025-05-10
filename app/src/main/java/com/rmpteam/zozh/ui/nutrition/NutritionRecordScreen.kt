package com.rmpteam.zozh.ui.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.nutrition.FakeMealDatasource
import com.rmpteam.zozh.di.AppViewModelProvider
import com.rmpteam.zozh.ui.component.DateTimePickerFieldToModal
import com.rmpteam.zozh.ui.theme.ZOZHTheme

@Composable
fun NutritionRecordScreen(
    modifier: Modifier = Modifier,
    //mealId: Int = 0,
) {
    val viewModel = viewModel<NutritionRecordViewModel>(factory = AppViewModelProvider.Factory)
    val uiState = viewModel.uiState

    if (!uiState.isMealFound) {
        Text(text = "Запись не найдена", modifier = modifier.padding(16.dp))
    } else {
        NutritionRecordScreenContent(
            modifier = modifier.verticalScroll(rememberScrollState()),
            mealUiState = uiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                if (uiState.isNewMeal) viewModel.createMeal()
                else viewModel.updateMeal()
            },
        )
    }
}

@Composable
fun NutritionRecordScreenContent(
    modifier: Modifier = Modifier,
    mealUiState: MealUiState,
    onValueChange: (MealRecord) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MealInputForm(
            meal = mealUiState.meal,
            onValueChange = onValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = mealUiState.isMealValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (mealUiState.isNewMeal) "Создать" else "Сохранить")
        }
    }
}

@Composable
fun MealInputForm(
    modifier: Modifier = Modifier,
    meal: MealRecord,
    onValueChange: (MealRecord) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateTimePickerFieldToModal(
            initialDateTime = meal.dateTime,
            onDateTimeSelected = { onValueChange(meal.copy(dateTime = it)) },
            label = { Text(text = "Дата и время") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = meal.name,
            onValueChange = { onValueChange(meal.copy(name = it)) },
            label = { Text(text = "Название") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        // TODO
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionRecordScreenPreview() {
    ZOZHTheme {
        NutritionRecordScreenContent(
            modifier = Modifier.fillMaxSize(),
            mealUiState = MealUiState(
                isNewMeal = false,
                meal = FakeMealDatasource.mealList[0].toMealRecord(),
                isMealValid = true,
                isMealFound = true
            )
        )
    }
}