package main.kotlin.entities

import java.sql.Date
import java.sql.ResultSet

class CarouselNews(resultSet: ResultSet) {
    private val id: Int = resultSet.getInt(1)
    private val title: String = resultSet.getString(2)
    private val description: String = resultSet.getString(3)
    private val date: Date = resultSet.getDate(4)
    private val url: String = resultSet.getString(5)
}