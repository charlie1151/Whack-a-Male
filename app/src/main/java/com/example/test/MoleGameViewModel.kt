package com.example.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MoleGameViewModel : ViewModel() {

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _timeRemaining = MutableStateFlow(60)
    val timeRemaining: StateFlow<Int> get() = _timeRemaining

    private val _moleIndices = MutableStateFlow<List<Int>>(emptyList()) // 同時可出現多隻地鼠
    val moleIndices: StateFlow<List<Int>> get() = _moleIndices

    private val _hammerIndex = MutableStateFlow(-1) // 槌子顯示位置
    val hammerIndex: StateFlow<Int> get() = _hammerIndex

    private val _gameResult = MutableStateFlow("") // "win", "lose", ""
    val gameResult: StateFlow<String> get() = _gameResult

    private var isGameRunning = true

    fun startGame() {
        //初始值
        _score.value = 0
        _timeRemaining.value = 60
        _gameResult.value = ""
        isGameRunning = true

        // 倒數計時邏輯
        viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000L)
                _timeRemaining.value -= 1
            }

            isGameRunning = false
            _moleIndices.value = emptyList()
            _gameResult.value = if (_score.value >= 700) "win" else "lose" // ★ 設 700 為 Win 判定
        }

        // 地鼠出現邏輯
        viewModelScope.launch {
            while (isGameRunning) {
                val time = _timeRemaining.value
                val moleCount = if (time > 30) 1 else 2
                val displayTime = if (time > 30) 800L else 1000L // ★ 前30秒 0.8秒，後30秒 1秒

                // 隨機地鼠位置
                val indices = (0 until 12).shuffled().take(moleCount)
                _moleIndices.value = indices

                delay(displayTime)

                // 清除地鼠
                if (isGameRunning) {
                    _moleIndices.value = emptyList()
                }
            }
        }
    }

    fun hitMole(position: Int) { //打擊地鼠
        if (_moleIndices.value.contains(position)) {
            _score.value += 10
            _moleIndices.value = _moleIndices.value - position
            _hammerIndex.value = position

            // 槌子圖持續 200ms ★
            viewModelScope.launch {
                delay(200L)
                _hammerIndex.value = -1
            }
        }
    }
}



