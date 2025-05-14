package com.rmpteam.zozh.ui.sleep

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepPhase
import com.rmpteam.zozh.data.sleep.SleepPhaseType
import com.rmpteam.zozh.data.sleep.SleepRepository
import com.rmpteam.zozh.data.sleep.SleepWithPhases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration

data class SleepDetailUiState(
    val sleepWithPhases: SleepWithPhases? = null,
    val isLoading: Boolean = true
)

class SleepDetailViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {
    var uiState by mutableStateOf(SleepDetailUiState())
        private set

    fun loadSleepDetails(sleepId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val sleep = sleepRepository.getSleepById(sleepId).first()

            if (sleep != null) {
                val sleepWithPhases = addMockSleepPhases(sleep)
                uiState = uiState.copy(sleepWithPhases = sleepWithPhases, isLoading = false)
            } else {
                uiState = uiState.copy(sleepWithPhases = null, isLoading = false)
            }
        }
    }

    private fun addMockSleepPhases(sleep: Sleep): SleepWithPhases {
        
        val phases = mutableListOf<SleepPhase>()

        var currentTime = sleep.startTime

        while (currentTime.isBefore(sleep.endTime)) {
            
            val lightEnd = currentTime.plusMinutes(45)
            if (lightEnd.isAfter(sleep.endTime)) break

            phases.add(
                SleepPhase(
                    startTime = currentTime,
                    endTime = lightEnd,
                    type = SleepPhaseType.LIGHT
                )
            )
            currentTime = lightEnd

            val deepEnd = currentTime.plusMinutes(30)
            if (deepEnd.isAfter(sleep.endTime)) break

            phases.add(
                SleepPhase(
                    startTime = currentTime,
                    endTime = deepEnd,
                    type = SleepPhaseType.DEEP
                )
            )
            currentTime = deepEnd

            val remEnd = currentTime.plusMinutes(15)
            if (remEnd.isAfter(sleep.endTime)) break

            phases.add(
                SleepPhase(
                    startTime = currentTime,
                    endTime = remEnd,
                    type = SleepPhaseType.REM
                )
            )
            currentTime = remEnd
        }

        return SleepWithPhases(sleep, phases)
    }
}