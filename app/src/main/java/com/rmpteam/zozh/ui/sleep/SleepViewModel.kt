package com.rmpteam.zozh.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepQuality
import com.rmpteam.zozh.data.sleep.SleepRepository
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

data class SleepUiState(
    val date: ZonedDateTime,
    val isLoading: Boolean = true,
    val sleepList: List<Sleep> = emptyList(),
    val isTrackingSleep: Boolean = false,
    val trackingStartTime: ZonedDateTime? = null
)

class SleepViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SleepUiState(date = DateTimeUtil.now()))
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    init {
        loadSleepData()
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            sleepRepository.getSleepByDate(_uiState.value.date).collect { sleepList ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sleepList = sleepList
                    )
                }
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
        val startTime = _uiState.value.trackingStartTime ?: return

        val endTime = startTime.plusHours(6)

        viewModelScope.launch {
            sleepRepository.insertSleep(
                Sleep(
                    startTime = startTime,
                    endTime = endTime,
                    quality = SleepQuality.GOOD
                )
            )

            _uiState.update {
                it.copy(
                    isTrackingSleep = false,
                    trackingStartTime = null
                )
            }

            loadSleepData()
        }
    }
}