package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var backButton: Button
    private var textString: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.search_button2)
        clearIcon = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button2)
        //Кнопочка назад

        backButton.setOnClickListener {
            finish()
        }

        //Отчистить строку
        clearIcon.setOnClickListener {
            editText.text.clear()
            val inputMeth =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMeth?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s?.isNotEmpty() ?: false)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val songsList = SongsAdapter(
            listOf(
                DataSongs("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
                DataSongs("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
                DataSongs("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
                DataSongs("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
                DataSongs("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")

            )
        )
        val recycleView = findViewById<RecyclerView>(R.id.our_recycle)
        recycleView.adapter = songsList

    }
    private fun clearButtonVisibility(isVisible: Boolean) {
        clearIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Хранение данных

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, textString)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textString = savedInstanceState.getString(SEARCH).toString()
        editText.setText(textString)
    }
   companion object {
      private  const val SEARCH = "TEXT"
    }
}