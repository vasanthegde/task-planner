package com.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent

fun Application.configureRouting() {

    routing {
        val records = mutableMapOf<Int, Task>()
        var taskId = 0

        get("/") {
            call.respond(ThymeleafContent("index", mapOf()))
        }

        post("/create") {
            taskId += 1
            val formParameters = call.receiveParameters()
            val task = getTask(formParameters)
            records[taskId] = task
            val rowMarkup = getTaskForm(task, taskId)
            call.respondText(rowMarkup)
        }

        post("/edit/{index}") {
            val index = call.parameters["index"]?.toInt() ?: -1
            if (records.containsKey(index)) {
                val task = records[index]!!
                val taskForm = """
                    <div>
                        <form hx-put="/update/$index" hx-target="closest div" hx-swap="outerHTML" >
                            <label>Title : <input name="title" value="${task.title}"> </label><br>
                            <label>Desc : <input name="desc" value="${task.desc}"> </label> <br>
                            <label>Story points : <input name="point" type="number" value="${task.point}"> </label> <br>
                            <label>Order : <input name="order" type="number" value="${task.order}"> </label> <br>
                            <label>Assignee :  <input name="assignee" value="${task.assignee}"></label><br>
                            <button type="submit"> Save </button>
                            </form>
                        </div>
                    """.trimIndent()
                call.respondText(taskForm)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid index")
            }
        }

        put("/update/{index}") {
            val index = call.parameters["index"]?.toInt() ?: -1
            println(call.request.headers["Content-Type"])
            if (records.containsKey(index)) {
                val formParameters = call.receiveParameters()
                val updatedTask = getTask(formParameters)
                records[index] = updatedTask
                val taskForm = getTaskForm(updatedTask, index)
                call.respondText(taskForm)
            }
        }
    }
}

