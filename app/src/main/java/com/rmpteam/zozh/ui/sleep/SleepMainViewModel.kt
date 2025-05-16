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

data class SleepMainUiState(
    val date: ZonedDateTime = DateTimeUtil.now(),
    val isLoading: Boolean = true,
    val sleepList: List<Sleep> = emptyList(),
    val isTrackingSleep: Boolean = false,
    val trackingStartTime: ZonedDateTime? = null
)

class SleepMainViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {
    private val initialDate = DateTimeUtil.now()

    private val _uiState = MutableStateFlow(SleepMainUiState(date = initialDate))
    val uiState: StateFlow<SleepMainUiState> = _uiState.asStateFlow()

    init {
        loadSleepData()
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            sleepRepository.getSleepByDate(_uiState.value.date).collect { sleepList ->
                _uiState.update { it.copy(isLoading = false, sleepList = sleepList) }
            }
        }
    }

    fun updateDate(date: ZonedDateTime) {
        _uiState.update { it.copy(date = date) }
        loadSleepData()
    }

    fun startTrackingSleep() {
        _uiState.update { it.copy(isTrackingSleep = true, trackingStartTime = DateTimeUtil.now()) }
    }

    fun stopTrackingSleep() {
        // mock implementation
        val newSleep = Sleep(
            startTime = _uiState.value.trackingStartTime?.minusHours(6)!!,
            endTime = DateTimeUtil.now(),
            quality = SleepQuality.GOOD,
            notes = "Автоматически созданная запись (демо)"
        )

        viewModelScope.launch {
            sleepRepository.insertSleep(newSleep)
            _uiState.update { it.copy(isTrackingSleep = false, trackingStartTime = null) }
        }
        loadSleepData()
    }

    fun isCurrentWeek(date: ZonedDateTime): Boolean {
        return ChronoUnit.DAYS.between(date, initialDate) in 0..6
    }
}