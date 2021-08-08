package com.bignerdranch.android.geomain

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    var isCheater = false
    private val questionBank = listOf(
        Question(R.string.question_australia, true, markedAnswer = false),
        Question(R.string.question_oceans, true, markedAnswer = false),
        Question(R.string.question_mideast, false, markedAnswer = false),
        Question(R.string.question_africa, false, markedAnswer = false),
        Question(R.string.question_americas, true, markedAnswer = false),
        Question(R.string.question_asia, true, markedAnswer = false)
    )
    private val MAX_CHEATS = 3

    var currentCheatCount = MAX_CHEATS

    val currentQuestionAnswer: Boolean
        get() =
            questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() =
            questionBank[currentIndex].textResId

    var currentMarkedQuestion: Boolean
        get() = questionBank[currentIndex].markedAnswer
        set(value) {
            questionBank[currentIndex].markedAnswer = value
        }

    var rightAnswers = 0
    var answeredQuestions = 0

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
    }

    val sizeOfArray: Int
        get() = questionBank.size

    fun eachValueOfArray() {
        for (question in questionBank) {
            question.markedAnswer = false
        }
    }
}