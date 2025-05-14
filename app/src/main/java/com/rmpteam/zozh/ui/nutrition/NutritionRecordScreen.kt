package com.rmpteam.zozh.ui.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.nutrition.FakeMealDatasource
import com.rmpteam.zozh.di.AppViewModelProvider
import com.rmpteam.zozh.ui.component.DateTimePickerFieldToModal
import com.rmpteam.zozh.ui.theme.ZOZHTheme
import kotlinx.coroutines.launch

@Composable
fun NutritionRecordScreen(
    modifier: Modifier = Modifier,
    //mealId: Int = 0,
    onNavigateBack: () -> Unit
) {
    val viewModel = viewModel<NutritionRecordViewModel>(factory = AppViewModelProvider.Factory)
    val uiState = viewModel.uiState

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackbarHostState, Modifier)

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
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Сохранено успешно!",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onDeleteClick = {
                viewModel.deleteMeal()
                onNavigateBack()
            },
        )
    }
}

@Composable
fun NutritionRecordScreenContent(
    modifier: Modifier = Modifier,
    mealUiState: MealUiState,
    onValueChange: (MealRecord) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        MealInputForm(
            meal = mealUiState.meal,
            onValueChange = onValueChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSaveClick,
            enabled = mealUiState.isMealValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (mealUiState.isNewMeal) "Создать" else "Сохранить")
        }
        if (!mealUiState.isNewMeal) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onDeleteClick,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Удалить")
            }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = if (meal.protein > 0) ""+meal.protein else "",
                onValueChange = { onValueChange(meal.copy(protein = it.toIntOrNull()?:0)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Белки") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = if (meal.fat > 0) ""+meal.fat else "",
                onValueChange = { onValueChange(meal.copy(fat = it.toIntOrNull()?:0)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Жиры") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = if (meal.carbs > 0) ""+meal.carbs else "",
                onValueChange = { onValueChange(meal.copy(carbs = it.toIntOrNull()?:0)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Углеводы") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
        }
        OutlinedTextField(
            value = ""+meal.calories,
            onValueChange = {  },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Калории") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = true,
            singleLine = true
        )
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