package com.example.percy.wakeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.math_view.*
import java.util.*

/**
 * Created by percy on 2018-03-06.
 */
class MathIssueActivity : AppCompatActivity() {

    private val TAG = MathIssueActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.math_view)

        val mult1 = Random().nextInt(10) + 1
        val mult2 = Random().nextInt(10) + 1

        multiplication_text.text = "$mult1 x $mult2 ?"

        submit_answer.setOnClickListener {
            var correctAnswer = mult1.times(mult2)

            val answer = if (answer_text.text.toString().isBlank()) 999 else answer_text.text.toString().toInt()

            if(answer == correctAnswer) {
                Log.d(TAG, "CORRECT ANSWER")
            } else {
                Toast.makeText(this,"Incorrect answer", Toast.LENGTH_LONG).show()
            }
        }

        go_back.setOnClickListener {
            super.onBackPressed()
        }
    }
}