package com.plugins

import io.ktor.http.Parameters
import kotlinx.serialization.Serializable
import java.time.LocalDate

fun getTask(param : Parameters): Task {
    val title = param["title"] ?: throw IllegalArgumentException("title parameter missing")
    val desc = param["desc"]
    val point = param["point"]?.toIntOrNull() ?: throw IllegalArgumentException("point parameter missing")
    val order = param["order"]?.toIntOrNull() ?: throw IllegalArgumentException("order parameter missing")
    val assignee = param["assignee"]
    return Task(title, desc, point, order, assignee)
}

fun visualize(tasks: List<Task>){
    val sorted  = tasks.sortedBy { it.order }
    val map = mutableMapOf<Task, LocalDate>()
    val startDate = LocalDate.now()
    var counter = 0L
    sorted.forEach {
        map.put(it, startDate.plusDays(it.point.toLong() + counter))
        counter += it.point
    }

}

fun getTaskForm(task: Task, index : Int): String{
    return """ 
                    <div>
                    <h4>Task #$index</h4>
                    <form hx-post="/edit/${index}" hx-target="closest div" hx-swap="outerHTML">
                        <p>title : ${task.title}</p>
                        <p>desc : ${task.desc}</p>
                        <p>story point : ${task.point}</p>
                        <p>order : ${task.order}</p>
                        <p>assignee : ${task.assignee}</p>
                        <h4><button type="submit" >Edit</button> </h4>
                    </form>
                    </div>
                """.trimIndent()
}


@Serializable
data class Task(val title: String, val desc: String?, val point: Int, val order :Int, val assignee : String?)