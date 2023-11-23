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


class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var clearIcon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.search_button2)
        clearIcon = findViewById(R.id.clearIcon)
        val backButton2 = findViewById<Button>(R.id.back_button2)
        //Кнопочка назад

        backButton2.setOnClickListener {
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

    }

    private fun clearButtonVisibility(isVisible: Boolean) {
        clearIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
    }



    // Хранение данных

    companion object {
        const val SEARCH = "TEXT"
    }
    private var textString: String = ""
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, textString)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textString = savedInstanceState.getString(SEARCH).toString()
        editText.setText(textString)
    }


}