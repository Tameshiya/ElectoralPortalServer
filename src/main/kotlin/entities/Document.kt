package main.kotlin.entities

import java.sql.ResultSet

class Document(resultSet: ResultSet) {

    private val name: String = resultSet.getString(1)
    private val url: String = resultSet.getString(2)

}
