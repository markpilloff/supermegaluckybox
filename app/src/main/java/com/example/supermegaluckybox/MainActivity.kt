package com.example.supermegaluckybox

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deck = Deck()

        val cardView: ImageView = findViewById(R.id.card_view)
        cardView.setImageResource(R.drawable.cardback)

        val numLeft: TextView = findViewById(R.id.num_left)
        numLeft.setText("${9 - deck.position} left")

        val nextCardButton: Button = findViewById(R.id.next_card_button)

        val wildSwitch: Switch = findViewById(R.id.go_wild)

        val x2View: ImageView = findViewById(R.id.x2)

        val nextCardAction = View.OnClickListener { _ ->
            val drawn = deck.draw()
            cardView.setImageResource(imageForCard(drawn, wildSwitch.isChecked()))
            numLeft.setText("${9 - deck.position} left")

            val countView: TextView = findViewById(viewIdForCardCount(drawn))
            countView.setBackgroundColor(colorForNumDrawn(deck.numDrawn(drawn)))
            if (deck.position > 8) {
                nextCardButton.setEnabled(false)
                cardView.setEnabled(false)
            }

            x2View.visibility = if (deck.atDouble()) View.VISIBLE else View.INVISIBLE
        }

        fun doShuffle(): Unit {
            deck.shuffle()
            cardView.setImageResource(imageForCard(-1, wildSwitch.isChecked()))
            resetCardCounts()
            numLeft.setText("${9 - deck.position} left")
            nextCardButton.setEnabled(true)
            cardView.setEnabled(true)
            x2View.visibility = View.INVISIBLE
        }

        nextCardButton.setOnClickListener(nextCardAction)
        cardView.setOnClickListener(nextCardAction)

        val shuffleButton: Button = findViewById(R.id.shuffle_button)
        shuffleButton.setOnClickListener {
            if (nextCardButton.isEnabled) {
                val dialogBuilder = AlertDialog.Builder(this)

                dialogBuilder.setMessage("There are more cards. Are you sure you want to shuffle?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                        run { doShuffle() }
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                        run { dialog.cancel() }
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Giving up so soon?")
                alert.show()
            } else {
                doShuffle()
            }
        }

    }

    // Resets the counter for each card value to 0.
    fun resetCardCounts() {
        (1..9).forEach {
            val countView: TextView = findViewById(viewIdForCardCount(it))
            countView.setBackgroundColor(colorForNumDrawn(0))
        }
    }

    fun viewIdForCardCount(draw: Int): Int {
        return when (draw) {
            1 -> R.id.view_1
            2 -> R.id.view_2
            3 -> R.id.view_3
            4 -> R.id.view_4
            5 -> R.id.view_5
            6 -> R.id.view_6
            7 -> R.id.view_7
            8 -> R.id.view_8
            else -> R.id.view_9
        }
    }

    fun imageForCard(draw: Int, isWild: Boolean): Int {
        if (isWild) {
            return when (draw) {
                1 -> R.drawable.animal1
                2 -> R.drawable.animal2
                3 -> R.drawable.animal3
                4 -> R.drawable.animal4
                5 -> R.drawable.animal5
                6 -> R.drawable.animal6
                7 -> R.drawable.animal7
                8 -> R.drawable.animal8
                9 -> R.drawable.animal9
                else -> R.drawable.cardback
            }
        } else {
            return when (draw) {
                1 -> R.drawable.card1
                2 -> R.drawable.card2
                3 -> R.drawable.card3
                4 -> R.drawable.card4
                5 -> R.drawable.card5
                6 -> R.drawable.card6
                7 -> R.drawable.card7
                8 -> R.drawable.card8
                9 -> R.drawable.card9
                else -> R.drawable.cardback
            }
        }
    }

    fun colorForNumDrawn(num: Int): Int {
        return when (num) {
            2 -> ContextCompat.getColor(this, R.color.gray)
            1 -> ContextCompat.getColor(this, R.color.yellow)
            else -> ContextCompat.getColor(this, R.color.green)
        }
    }

}

class Deck {
    var cards = IntArray(18, { i -> i / 2 + 1 })
    var position = 0

    init {
        shuffle()
    }

    // Shuffles the deck and resets the position.
    fun shuffle() {
        cards.shuffle()
        position = 0
    }

    // Returns the current card; advances position to next.
    fun draw(): Int {
        // Should check position
        return cards[position++]
    }

    // Returns the number of times that card has been seen.
    fun numDrawn(card: Int): Int {
        return Collections.frequency(cards.slice(0..position - 1), card)
    }

    fun atDouble(): Boolean {
        return position > 1 && cards[position - 2] == cards[position - 1]
    }
}