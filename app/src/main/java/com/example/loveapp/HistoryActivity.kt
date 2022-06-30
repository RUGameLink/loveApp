package com.example.loveapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val namesData = intent.getStringArrayListExtra( "namesData" )//Получение данных из другой активити
        val resultData = intent.getStringArrayListExtra( "resultData" )//Получение данных из другой активити

        namesData?.reverse()
        resultData?.reverse()

        val recyclerView: RecyclerView = findViewById(R.id.historyView)//Подвязка ресайклера к объекту
        val linearLayoutManager = LinearLayoutManager(applicationContext)//Подготовка лайаут менеджера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager//Инициализация лайаут менеджера
        recyclerView.adapter = CustomRecyclerAdapter(namesData!!, resultData!!)//внесение данных из листа в адаптер (заполнение данными)
    }
}