// app/src/main/java/com/rmpteam/zozh/ui/sleep/SleepViewModel.kt
package com.rmpteam.zozh.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepRepository
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.ZonedDateTime

data class SleepUiState(
    val date: ZonedDateTime,
    val isLoading: Boolean = true,
    val sleepList: List<Sleep> = emptyList(),
)

class SleepViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {
    private val _dateState = MutableStateFlow(DateTimeUtil.now())

    val uiState: StateFlow<SleepUiState> = _dateState
        .flatMapLatest { date ->
            sleepRepository.getSleepByDate(date)
                .map { SleepUiState(date = date, isLoading = false, sleepList = it) }
                .onStart { emit(SleepUiState(date = date, isLoading = true)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = SleepUiState(date = _dateState.value)
        )

    fun updateDate(date: ZonedDateTime) {
        _dateState.value = date
    }
}