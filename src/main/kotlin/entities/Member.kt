package main.kotlin.entities

import java.sql.ResultSet

class Member(resultSet: ResultSet) {

    private val id: Int = resultSet.getInt(1)
    private val name: String = "${resultSet.getString(2)} ${resultSet.getString(3)} ${resultSet.getString(4)} "
    private val post: String = resultSet.getString(5)
    private val offeredBy: String = resultSet.getString(6)
    private val avatarUrl: String = resultSet.getString(7)

}
