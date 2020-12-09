package com.shen.variant_gson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.variant_gson.core.GsonBuilder
import com.variant_gson.core.SerializedAdapter
import com.variant_gson.core.annotations.SerializedName

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = GsonBuilder().setSerializedAdapter(object : SerializedAdapter {
            override fun adapter(annotation: SerializedName, defaultName: String): String {
                return "123456"
            }

            override fun adapterAlternate(annotation: SerializedName, defaultAlternate: Array<String>): Array<String> {
                return defaultAlternate
            }
        }).create()

        Log.e("Lalala", gson.toJson(Temp("2")))
    }

    data class Temp(
            @SerializedName("123") val name: String
    )
}