package com.example.loveapp

import android.provider.BaseColumns

object DbName: BaseColumns {//Набор констант для бд
    const val TABLE_NAME = "history"//Имя таблицы
    const val COLUMN_NAME_TITLE = "names"//Имя колонки
    const val COLUMN_NAME_SUBTITLE = "result"//Имя колонки

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "history.db"//Название бд

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ("+
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_NAME_TITLE TEXT, " +
            "$COLUMN_NAME_SUBTITLE TEXT)"//Константа запроса создания таблицы

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"//Константа запроса удаления таблицы

}