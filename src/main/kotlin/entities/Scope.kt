package main.kotlin.entities

import java.sql.ResultSet

class Scope(resultSet: ResultSet) {

    private val id: Int = resultSet.getInt(1)
    private val name: String = resultSet.getString(2)
}
