package com.rmpteam.zozh.data.physicalActivity

import com.rmpteam.zozh.ui.physicalActivity.Workout
import kotlin.random.Random


class FakeActivityRepository {
    private val workouts = listOf(
        Workout("Бег", "30 мин"),
        Workout("Велосипед", "45 мин"),
        Workout("Ходьба", "20 мин"),
        Workout("Плавание", "60 мин"),
        Workout("Йога", "40 мин"),
        Workout("Баскетбол", "50 мин"),
        Workout("Волейбол", "45 мин"),
        Workout("Гандбол", "30 мин"),
        Workout("Бейсбол", "70 мин"),
        Workout("Пилатес", "20 мин"),
        Workout("Фитнес", "60 мин"),
        Workout("Теннис", "80 мин"),
        )

    private val heartRates = listOf(65, 70, 72, 75, 80, 85, 90, 100, 120, 135, 140)
    private val bloodPressures = listOf(120 to 80, 130 to 85, 125 to 75, 140 to 90, 115 to 75)

    fun getLastThreeActivities(): List<Workout> {
        return workouts.shuffled().take(3)
    }
    fun getLastZeroActivities(): List<Workout> {
        return workouts.shuffled().take(0)
    }

    fun getCurrentHeartRate(): Int {
        return heartRates.random()
    }

    fun getSteps(): Int {
        return (2000..10000).random()
    }

    fun getCalories(): Int {
        return (50..1000).random()
    }

    fun getHeartPressure(): Pair<Int, Int> {
        return bloodPressures.random()
    }
}