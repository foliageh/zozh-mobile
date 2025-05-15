package com.rmpteam.zozh.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepQuality
import com.rmpteam.zozh.data.sleep.SleepRepository
import com.rmpteam.zozh.util.DateTimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class SleepUiState(
    val date: ZonedDateTime = DateTimeUtil.now(),
    val isLoading: Boolean = true,
    val sleepList: List<Sleep> = emptyList(),
    val isTrackingSleep: Boolean = false,
    val trackingStartTime: ZonedDateTime? = null
)

class SleepViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {
    
    private val currentDate = DateTimeUtil.now()

    private var currentSleepList = listOf<Sleep>()

    private val _uiState = MutableStateFlow(SleepUiState(date = currentDate))
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    init {
        loadSleepData()
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            sleepRepository.getSleepByDate(_uiState.value.date).collect { sleepList ->
                currentSleepList = sleepList

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        sleepList = sleepList
                    )
                }

                println("Загружено записей о сне: ${sleepList.size}")
            }
        }
    }

    fun updateDate(date: ZonedDateTime) {
        _uiState.update { it.copy(date = date) }
        loadSleepData()
    }

    fun startTrackingSleep() {
        _uiState.update {
            it.copy(
                isTrackingSleep = true,
                trackingStartTime = DateTimeUtil.now()
            )
        }
    }

    fun stopTrackingSleep() {
        
        val startTime = DateTimeUtil.now().minusHours(6)
        val endTime = DateTimeUtil.now()

        val newSleep = Sleep(
            id = (currentSleepList.maxOfOrNull { it.id } ?: 0) + 1,
            startTime = startTime,
            endTime = endTime,
            quality = SleepQuality.GOOD,
            notes = "Автоматически созданная запись (демо)"
        )

        viewModelScope.launch {
            try {
                sleepRepository.insertSleep(newSleep)
                println("Добавлена запись о сне (через репозиторий)")
            } catch (e: Exception) {
                println("Ошибка при добавлении записи: ${e.message}")
            }

            val updatedList = currentSleepList.toMutableList()
            updatedList.add(newSleep)
            currentSleepList = updatedList

            _uiState.update {
                it.copy(
                    isTrackingSleep = false,
                    trackingStartTime = null,
                    sleepList = updatedList
                )
            }

            println("Локальное состояние обновлено, записей: ${updatedList.size}")
        }
    }

    fun isCurrentWeek(date: ZonedDateTime): Boolean {
        
        val daysDifference = ChronoUnit.DAYS.between(date, currentDate)

        return daysDifference in 0..6
    }
}