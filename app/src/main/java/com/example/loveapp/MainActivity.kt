package com.example.loveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    //Инициализация переменных для объектов лайаута
    private lateinit var nameOneText: EditText
    private lateinit var nameTwoText: EditText
    private lateinit var checkResButton: Button
    private lateinit var historyButton: Button
    private lateinit var textLoading: TextView
    private lateinit var resText: TextView
    private lateinit var namesText: TextView

    lateinit var handler: Handler
    private lateinit var progressBar: ProgressBar
    private val dbManager = DbManager(this) //Инициализация бд-менеджера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        checkResButton.setOnClickListener { //Слушатель кнопки Проверки совместимости
            if (nameOneText.text.isEmpty() || nameTwoText.text.isEmpty()){ //Если текстовое поля пустые, то закончить выполнение функции
                Toast.makeText(this, getText(R.string.error_message), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{ //Продолжение работы в случае заполненных полей
                var nameOne = replaceString(nameOneText.text.toString()) //Передача текста в метод изменения строки
                var nameTwo = replaceString(nameTwoText.text.toString()) //Передача текста в метод изменения строки

                resText.text = ""
                namesText.text = ""

                handler = Handler()

                Toast.makeText(this, getText(R.string.loading), Toast.LENGTH_SHORT).show() //Вывод тоста для обозначения начала работы

                val thread = Thread{ //Открытие потока
                    try {
                        //Работа с api
                        val client = OkHttpClient()

                        val request = Request.Builder()
                            .url("https://love-calculator.p.rapidapi.com/getPercentage?sname=${nameOne}&fname=${nameTwo}") //Формирование запроса
                            .get()
                            .addHeader("X-RapidAPI-Host", "love-calculator.p.rapidapi.com") //Обращение к api
                            .addHeader("X-RapidAPI-Key", "8fac8d93edmshc4380d7d88505cdp17d5dfjsndf4c3b2501a4") //Авторазация пользователя в api
                            .build()

                        val response = client.newCall(request).execute() //Отправка запроса в api
                        val result = response.body()?.string() //Получение результатов в видо json файла

                        //Вытягивание данных из результата запроса
                        var fName = JSONObject(result).getString("fname")
                        var sName = JSONObject(result).getString("sname")
                        var percentage = JSONObject(result).getString("percentage").toInt()
                        var resultText = JSONObject(result).getString("result")

                        var i = 0
                        while (i <= percentage) {
                            i += 1

                            handler.post(Runnable {
                                progressBar.progress = i //Работа в отдельном потоке прогрессбара

                                textLoading.setText("${getText(R.string.compatibility)} ${i}%")
                            })
                            try {
                                Thread.sleep(27)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        handler.postDelayed({
                        },0)

                        runOnUiThread { //Возврат в основной поток активити
                            //Запись в textview результатов запроса
                            resText.text = resultText
                            namesText.text = "$fName + $sName"

                            //Запись истории поиска в бд
                            dbManager.openDb() //Открытие бд
                            dbManager.insertToDb(namesText.text.toString(), resText.text.toString()) //Запись
                            dbManager.closeDb() //Закрытие бд
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }
                thread.start() //Открытие потока
            }
        }

        historyButton.setOnClickListener { //Слушатель кнопки Истории
            dbManager.openDb() //Открытие бд
            val namesData = dbManager.readDbDataNames() //Считывание из бд колонки имен в лист
            val resultData = dbManager.readDbDataResult() //Считывание из бд колонки результата в лист
            dbManager.closeDb() //Закрытие бд

            val i = Intent(this, HistoryActivity::class.java) //Инициализация интента для открытия новой активити
            i.putStringArrayListExtra("namesData", namesData) //Добавление в интент листов с соответсткующими ключами
            i.putStringArrayListExtra("resultData", resultData)
            startActivity(i) //Старт активити
        }
    }

    private fun replaceString(text: String): String{
        //Изменение пробелов на смвол "%20" для корректной работы запроса
        return text.replace(" ", "%20", false)
    }

    private fun init(){
        nameOneText = findViewById(R.id.nameOneText)
        nameTwoText = findViewById(R.id.nameTwoText)
        checkResButton = findViewById(R.id.checkResButton)
        historyButton = findViewById(R.id.historyButton)
        progressBar = findViewById(R.id.progressBar)
        textLoading = findViewById(R.id.textLoading)
        resText = findViewById(R.id.resText)
        namesText = findViewById(R.id.namesText)
    }
}