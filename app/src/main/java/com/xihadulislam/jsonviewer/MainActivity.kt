package com.xihadulislam.jsonviewer

import android.os.Bundle
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclewView: JsonView
    private lateinit var mHScrollView: HorizontalScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mHScrollView = findViewById(R.id.hsv)

        mRecyclewView = findViewById(R.id.rv_json)
        mRecyclewView.setScaleEnable(true)
        Thread {
            val fileInString: String =
                applicationContext.assets.open("demo.json").bufferedReader().use { it.readText() }
            runOnUiThread {
                mRecyclewView.bindJson(fileInString)
            }

        }.start()
    }

}