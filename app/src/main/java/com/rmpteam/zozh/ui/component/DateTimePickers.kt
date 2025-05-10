package com.rmpteam.zozh.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.DateTimeUtil.dateTimeString
import com.rmpteam.zozh.util.DateTimeUtil.toEpochMilli
import com.rmpteam.zozh.util.DateTimeUtil.zoneOffsetMilli
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    dateTime: ZonedDateTime,
    onConfirm: (ZonedDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateTime.toEpochMilli() + dateTime.zoneOffsetMilli()
    )

    DatePickerDialog(
        confirmButton = {
            TextButton(onClick = {
                onConfirm(DateTimeUtil.epochMilliToDateTime(datePickerState.selectedDateMillis!! - dateTime.zoneOffsetMilli()))
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        onDismissRequest = onDismiss,
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    dateTime: ZonedDateTime,
    onConfirm: (ZonedDateTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = dateTime.hour,
        initialMinute = dateTime.minute,
        is24Hour = true
    )

    var showDial by remember { mutableStateOf(true) }

    val toggleIcon = if (showDial) Icons.Filled.EditCalendar
    else Icons.Filled.AccessTime

    TimePickerDialog(
        onConfirm = { onConfirm(dateTime.withHour(timePickerState.hour).withMinute(timePickerState.minute).withSecond(0)) },
        onDismiss = onDismiss,
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Переключатель типа выбора времени",
                )
            }
        },
    ) {
        if (showDial) TimePicker(state = timePickerState)
        else TimeInput(state = timePickerState)
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

@Composable
fun DateTimePickerFieldToModal(
    modifier: Modifier = Modifier,
    initialDateTime: ZonedDateTime = DateTimeUtil.now(),
    onDateTimeSelected: (ZonedDateTime) -> Unit,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = false
) {
    var showDateModal by remember { mutableStateOf(false) }
    var showTimeModal by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf(initialDateTime) }

    OutlinedTextField(
        value = selectedDateTime.dateTimeString(),
        onValueChange = { },
        label = label,
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Выберите дату и время")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(showDateModal, showTimeModal) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    if (waitForUpOrCancellation(pass = PointerEventPass.Initial) != null) {
                        if (enabled) showDateModal = true
                    }
                }
            },
        enabled = enabled,
        readOnly = true,
        singleLine = singleLine
    )

    if (showDateModal) {
        DatePickerModal(
            dateTime = selectedDateTime,
            onConfirm = { dateTime ->
                selectedDateTime = dateTime.withHour(selectedDateTime.hour).withMinute(selectedDateTime.minute)
                showDateModal = false
                showTimeModal = true
            },
            onDismiss = { showDateModal = false }
        )
    }

    if (showTimeModal) {
        TimePickerModal(
            dateTime = selectedDateTime,
            onConfirm = { dateTime ->
                selectedDateTime = dateTime
                onDateTimeSelected(selectedDateTime)
                showTimeModal = false
            },
            onDismiss = { showTimeModal = false }
        )
    }
}