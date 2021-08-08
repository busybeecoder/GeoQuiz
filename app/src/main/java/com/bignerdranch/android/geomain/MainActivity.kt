package com.bignerdranch.android.geomain

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val KEY_CHEAT_COUNT = "cheat_count"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var cheatNumberTextView: TextView


//    private var whichAnswered = BooleanArray(questionBank.size)

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val currentCheatCount = savedInstanceState?.getInt(KEY_CHEAT_COUNT, 0) ?: 0

        quizViewModel.currentIndex = currentIndex
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton = findViewById(R.id.false_button)
        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        cheatButton = findViewById(R.id.cheat_button)
        cheatButton.setOnClickListener {
            quizViewModel.currentCheatCount--
            if (currentCheatCount <= 0) {
                cheatButton.isEnabled = false
            }
            cheatNumberTextView.text = "Remaining cheats: ${quizViewModel.currentCheatCount.toString()}"
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = ActivityOptions.makeClipRevealAnimation(it, 0,0, it.width, it.height)
            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
        }

        cheatNumberTextView = findViewById(R.id.remaining_cheats_text_view)
        cheatNumberTextView.text = "Remaining cheats: ${quizViewModel.currentCheatCount.toString()}"

        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            quizViewModel.isCheater = false
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton = findViewById(R.id.prev_button)
        prevButton.setOnClickListener {
            if (quizViewModel.currentIndex == 0) {
                quizViewModel.currentIndex = quizViewModel.sizeOfArray
            }

            quizViewModel.moveToPrev()

            if (quizViewModel.currentIndex < 0) {
                quizViewModel.currentIndex = quizViewModel.sizeOfArray
            }
            quizViewModel.isCheater = false
            updateQuestion()
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(
            TAG,
            "onStart() called"
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            TAG,
            "onResume() called"
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(
            TAG,
            "onPause() called"
        )
    }

    override fun onStop() {
        super.onStop()
        Log.d(
            TAG,
            "onStop() called"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(
            TAG,
            "onDestroy() called"
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CHEAT_COUNT, quizViewModel.currentCheatCount)
    }

    private fun buttonsEnabled(enabled: Boolean) {
        trueButton.isEnabled = enabled
        falseButton.isEnabled = enabled
    }

    private fun updateQuestion() {
        val questionTextById = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextById)
        if (!quizViewModel.currentMarkedQuestion) {
            buttonsEnabled(true)
        } else {
            buttonsEnabled(false)
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        quizViewModel.currentMarkedQuestion = true
        buttonsEnabled(false)
        quizViewModel.answeredQuestions++


        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        if (userAnswer == correctAnswer) {
            quizViewModel.rightAnswers++
        }
        val toast = Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
        calculateScore()
    }

    private fun calculateScore() {
        val totalQuestions = quizViewModel.sizeOfArray
        val score = quizViewModel.rightAnswers * 100 / totalQuestions
        if (quizViewModel.answeredQuestions == totalQuestions) {
            val message = "You gained $score!"
            val toast = Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            quizViewModel.rightAnswers = 0
            quizViewModel.answeredQuestions = 0
            quizViewModel.eachValueOfArray()
        }
    }
}