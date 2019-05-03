package main.kotlin

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import main.kotlin.entities.CarouselNews
import main.kotlin.entities.News

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

//@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {


    val url = "jdbc:postgresql://localhost/electoral_portal"
    val props = java.util.Properties()
    props.setProperty("user", "postgres")
    props.setProperty("password", "123")
    props.setProperty("ssl", "false")
    val connection = java.sql.DriverManager.getConnection(url, props)
    val gson = Gson()

    routing {
        get("/") {
            call.respondText("HELLO FROM MY AWESOME SERVER!", contentType = ContentType.Text.Plain)
        }

        get("/news") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM news ORDER BY date DESC LIMIT 4")
            val result: MutableList<News> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(News(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        get("/news/{id}") {
            val newsId = call.parameters["id"]!!.toInt()
            val statement = connection.prepareStatement("SELECT * FROM news WHERE id = ?")
            statement.setInt(1, newsId)
            val resultSet = statement.executeQuery()
            resultSet.next()
            val result = News(resultSet)
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        get("/carousel") {
            val limit = call.parameters["limit"]?.toInt()
            val statement = connection.prepareStatement("""
                SELECT n.id, n.title, n.description, n.date, np.url FROM news n
                JOIN news_photos np ON n.id = np.news_id
                ORDER BY n.id DESC
                LIMIT ?;
            """)
            statement.setInt(1, limit ?: 5)
            val resultSet = statement.executeQuery()
            val result: MutableList<CarouselNews> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(CarouselNews(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        /*get("/users") {
            connection.createStatement().use {
                it.executeQuery(
                    " SELECT * FROM users;"
                ).use{
                    val users: MutableList<User> = java.util.ArrayList()
                    while (it.next()) {
                        users.add(User(it))
                    }
                    call.respondText(gson.toJson(users), contentType = ContentType.Application.Json)
                }
            }
        }

        get("/employee") {
            val employeeId: Int? = call.parameters["id"]!!.toInt()
            val employeePassphrase: String? = call.parameters["passphrase"]
            val statement = connection.prepareStatement("SELECT * FROM employees WHERE id = ? AND passphrase = ?")
            statement.setInt(1, employeeId!!)
            statement.setString(2, employeePassphrase!!)
            val resultSet = statement.executeQuery()
            resultSet.next()
            val result = Employee(resultSet)
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }
        get("/employees") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM employees")
            val result: MutableList<Employee> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(Employee(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        post("/order") {
            val payload = call.receiveText()
            val user = gson.fromJson<OrderedUser>(payload, OrderedUser::class.java)
            val statement = connection.prepareStatement("""
                INSERT INTO orders (customer_name, customer_email, customer_phone, message) VALUES(?, ?, ?, ?)
            """.trimIndent())
            statement.setString(1, user.name)
            statement.setString(2, user.email)
            statement.setString(3, user.phone)
            statement.setString(4, user.message ?: "")
            call.respond(if (statement.execute()) HttpStatusCode.OK else HttpStatusCode.BadRequest)
        }

        get("/orders") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM orders")
            val result: MutableList<Order> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(Order(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        get("/prices") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM prices")
            val result: MutableList<Price> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(Price(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        post("/register") {
            val payload = call.receiveText()
            kotlin.io.println(payload)
            val user = gson.fromJson<RegistrationUser>(payload, RegistrationUser::class.java)
            val statement = connection.prepareStatement("""
                INSERT INTO users (name, age, gender, description) VALUES(?, ?, ?, ?);
                INSERT INTO news (title, id_author, short_text, full_text)
                    VALUES ('Регистрация нового пользователя', 4, ?, ?);
            """.trimIndent())
            statement.setString(1, user.name)
            statement.setInt(2, user.age)
            statement.setString(3, user.gender)
            statement.setString(4, user.description)
            statement.setString(5, "Приветствуем нового пользователя с именем " + user.name)
            statement.setString(6, "Приветствуем нового пользователя с именем " + user.name +
                    ". Ему на данный момент " + user.age + if (user.description.isEmpty()) ""
            else ". Вот пару фактов о новичке: \"" +  user.description + "\"")
            call.respond(if (statement.execute()) HttpStatusCode.OK else HttpStatusCode.BadRequest)
        }

        post("/news") {
            val payload = call.receiveText()
            kotlin.io.println(payload)
            val news = gson.fromJson<NewNews>(payload, NewNews::class.java)
            val statement = connection.prepareStatement("""
                INSERT INTO news (title, id_author, short_text, full_text)
                    VALUES (?, ?, ?, ?)
            """.trimIndent())
            statement.setString(1, news.title)
            statement.setInt(2, news.authorId)
            statement.setString(3, news.desc)
            statement.setString(4, news.fullText)
            call.respond(if (statement.execute()) HttpStatusCode.OK else HttpStatusCode.BadRequest)
        }

        get("/news") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM news ORDER BY date DESC LIMIT 4")
            val result: MutableList<News> = java.util.ArrayList()
            while (resultSet.next()) {
                result.add(News(resultSet))
            }
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        get("/news/{id}") {
            val newsId = call.parameters["id"]!!.toInt()
            val statement = connection.prepareStatement("SELECT * FROM news WHERE id = ?")
            statement.setInt(1, newsId)
            val resultSet = statement.executeQuery()
            resultSet.next()
            val result = News(resultSet)
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }

        get("/userByName/{name}") {
            var userName = call.parameters["name"]?.replace('+', ' ', false)
            val statement = connection.prepareStatement("SELECT * FROM users WHERE lower(name) = lower(?)")
            statement.setString(1, userName)
            val resultSet = statement.executeQuery()
            resultSet.next()
            val result = User(resultSet)
            call.respondText(gson.toJson(result), contentType = ContentType.Application.Json)
        }*/
    }
}