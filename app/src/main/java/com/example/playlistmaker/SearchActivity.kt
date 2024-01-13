package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private val songAdapter = SongsAdapter()
    private lateinit var placeholderMessage: TextView
    private lateinit var editText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var backButton: Button
    private var textString: String = ""
    private lateinit var iconNothingFound: ImageView
    private lateinit var iconNoInternet: ImageView
    private lateinit var buttonRefresh: Button
    private val baseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(iTunesApi::class.java)
    private val songs = ArrayList<DataSongs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.search_button2)
        clearIcon = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button2)

        iconNothingFound = findViewById(R.id.placeholder_nothing_f)
        iconNoInternet = findViewById(R.id.placeholder_no_inet)
        buttonRefresh = findViewById(R.id.ref_button)
        placeholderMessage = findViewById(R.id.placeh_text)
        //Кнопочка назад

        backButton.setOnClickListener {
            finish()
        }

        //Поиск

        buttonRefresh.setOnClickListener {
            search()
        }

        //Отчистить строку
        clearIcon.setOnClickListener {
            editText.text.clear()
            val inputMeth =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMeth?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        val textWatch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s?.isNotEmpty() ?: false)
                textString = editText.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        val recycleView = findViewById<RecyclerView>(R.id.our_recycle)
        editText.addTextChangedListener(textWatch)
        recycleView.adapter = songAdapter
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
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

    private fun showMessage(text: String, additionalMessage: String) {
        if(text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            songAdapter.track.clear()
            songAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if(additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun showPlaceHolder(text: String)  {
        when(text) {
            getString(R.string.no_conection) -> {
                iconNothingFound.visibility = View.GONE
                iconNoInternet.visibility = View.VISIBLE
                buttonRefresh.visibility = View.VISIBLE
            }
            getString(R.string.nothing_found) -> {
                iconNothingFound.visibility = View.VISIBLE
                iconNoInternet.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }
            else -> {
                iconNothingFound.visibility = View.GONE
                iconNoInternet.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }

        }
    }

    private fun search() {
        if(editText.text.isNotEmpty()) {
            imdbService.search(editText.text.toString()).enqueue(object : Callback<TrackResponse>{
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if(response.code() == 200) {
                        songAdapter.track.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            songAdapter.track.addAll(response.body()?.results!!)
                            songAdapter.notifyDataSetChanged()
                        }
                        if(songAdapter.track.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                            showPlaceHolder(getString(R.string.nothing_found))
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(getString(R.string.no_conection), response.code().toString())
                        showPlaceHolder(getString(R.string.no_conection))
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.no_conection), t.message.toString())
                    showPlaceHolder(getString(R.string.no_conection))
                }
            })
        }
    }
}