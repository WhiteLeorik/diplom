import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.roundToInt

data class User(val id: Int, var nickname: String, var freeTime: Array<LocalTime>, var dayoffNumber: Array<Int>)

data class Task(
    var priority: Int,
    var name: String,
    var description: String,
    var durationTime: LocalTime,
    var beginTime: LocalDateTime,
    var endTime: LocalDateTime
) : Comparable<Task> {
    constructor(priority: Int, name: String, description: String, durationHours: LocalTime) : this(
        priority,
        name,
        description,
        durationHours,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    init {
        endTime = beginTime.plusHours(durationTime.hour.toLong())
        endTime.plusMinutes(durationTime.minute.toLong())
    }

    override fun compareTo(other: Task): Int = this.priority.compareTo(other.priority)
}

fun main() {
    val george = User(1, "George", arrayOf(LocalTime.of(12, 30), LocalTime.of(17, 20)), arrayOf(1, 2, 7))
    val tasks = arrayOf(
        Task(2, "Имя", "Описание задачи", LocalTime.of(1, 30)),
        Task(1, "Имя2", "Описание задачи2", LocalTime.of(1, 30)),
        Task(3, "Имя3", "Описание задачи4", LocalTime.of(3, 10)),
        Task(10, "Имя4", "Описание задачи", LocalTime.of(1, 0)),
        Task(5, "Имя", "Описание задачи", LocalTime.of(0, 30))
    )

    if (tasks.isNotEmpty() && !dayoffCheck(george)) {
        tasks.sort()
        //var temp = taskAssignment(george, tasks)
        //for (i in temp.indices) println(temp[i])
        println(localTimeToDouble( LocalTime.of(17,59)))
        println(periodLocalTime(LocalTime.of(1,29), LocalTime.of(1,59)))
    }
}

fun taskAssignment(
    user: User,
    tasks: Array<Task>
): Array<Task> //Метод выборки задач, которые в приоритете и помещаются в свободное время пользователя
{
    val plugTask = Task(1, "Plug", " ", LocalTime.of(0, 0))
    var currentTasks: Array<Task> = arrayOf(plugTask)
    var taskDuration: Double = localTimeToDouble(tasks[0].durationTime)
    val freeTime: Double = periodLocalTime(user.freeTime[0], user.freeTime[1])
    for (i in tasks.indices) {
        if (freeTime > taskDuration) taskDuration += localTimeToDouble(tasks[i + 1].durationTime) else {
            for (x in 1..i) {
                currentTasks = currentTasks.plus(tasks[x - 1])
            }
            break
        }
    }
    return currentTasks
}

fun dayoffCheck(user: User): Boolean {
    for (i in user.dayoffNumber.indices) {
        if (LocalDateTime.now().dayOfWeek.value == user.dayoffNumber[i]) return true
    }
    return false
}

fun localTimeToDouble(time: LocalTime): Double {
    return time.hour + (time.minute * 0.01)
}

fun periodLocalTime(firstTime: LocalTime, lastTime: LocalTime): Double {
    var firstTimeDouble: Double = localTimeToDouble(firstTime)
    var lastTimeDouble: Double = localTimeToDouble(lastTime)
    if (firstTimeDouble.toInt() != 0) if ((firstTimeDouble * 100).mod(100.0) == 0.0) firstTimeDouble =
        firstTimeDouble.toInt() - 1 + 0.60 else {
    } else if ((firstTimeDouble * 100).mod(100.0) == 0.0) firstTimeDouble = 23.60 else firstTimeDouble += 0.0
    if (lastTimeDouble.toInt() != 0) if ((lastTimeDouble * 100).mod(100.0) == 0.0) lastTimeDouble =
        lastTimeDouble.toInt() - 1 + 0.60 else {
    } else if ((lastTimeDouble * 100).mod(100.0) == 0.0) lastTimeDouble = 23.60 else lastTimeDouble += 24.0
    val minutes: Double =
        if ((lastTimeDouble * 100).mod(100.0) < (firstTimeDouble * 100).mod(100.0)) ((firstTimeDouble * 100).mod(100.0) - (lastTimeDouble * 100).mod(
            100.0
        )) * 0.1 else ((lastTimeDouble * 100).mod(100.0) - (firstTimeDouble * 100).mod(100.0)) * 0.01
    return abs(lastTimeDouble.toInt()- firstTimeDouble.toInt()) + minutes
}

fun sumLocalTime(firstTime: Double, lastTime: Double): Double {
    var result: Double = 0.0
    result =
        if ((firstTime * 100).mod(100.0) + (lastTime * 100).mod(100.0) >= 60) firstTime.toInt() + lastTime.toInt() + 1.0 + (((firstTime * 100).mod(
            100.0
        ) + (lastTime * 100).mod(
            100.0
        ) - 60)) * 0.01

        else firstTime + lastTime
    return result
}

//Сделать проверку на выходные из времени интернета, а если попытка не удалась то из времени устройства

//Сделать проверку на праздничные дни
//Сделать проверку на пересчет задач в середине рабочего времени