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
        priority, name, description, durationHours, LocalDateTime.now(), LocalDateTime.now()
    )

    init {
        endTime = beginTime.plusHours(durationTime.hour.toLong())
        endTime.plusMinutes(durationTime.minute.toLong())
    }

    override fun compareTo(other: Task): Int = this.priority.compareTo(other.priority)
}

fun main() {
    val george = User(
        1,
        "George",
        arrayOf(
            LocalTime.of(0, 0),
            LocalTime.of(0, 0),
                LocalTime.of(0, 0),
                LocalTime.of(0, 0),
            LocalTime.of(12, 30),
            LocalTime.of(17, 20),
            LocalTime.of(12, 30),
            LocalTime.of(17, 20),
            LocalTime.of(12, 30),
            LocalTime.of(17, 20),
            LocalTime.of(12, 30),
            LocalTime.of(17, 20),
                LocalTime.of(0, 0),
                LocalTime.of(0, 0)
        ),
        arrayOf(1, 2, 7)
    )
    val tasks = arrayOf(
        Task(2, "Имя", "Описание задачи", LocalTime.of(1, 30)),
        Task(1, "Имя2", "Описание задачи2", LocalTime.of(1, 30)),
        Task(3, "Имя3", "Описание задачи4", LocalTime.of(3, 10)),
        Task(10, "Имя4", "Описание задачи", LocalTime.of(1, 0)),
        Task(5, "Имя", "Описание задачи", LocalTime.of(0, 30))
    )

    if (tasks.isNotEmpty()) {
        tasks.sort()
        val currentTasks = taskAssignment(george,tasks)
        for(i in currentTasks.indices) println(currentTasks[i])

    }
}

fun taskAssignment(
    user: User, tasks: Array<Task>
): Array<Task> //Метод выборки задач, которые в приоритете и помещаются в свободное время пользователя
{
    val plugTask = Task(1, "Plug", " ", LocalTime.of(0, 0))
    var currentTasks = arrayOf(plugTask)
    var freeTime = 0.0
    var taskDuration = localTimeToDouble(tasks[0].durationTime)
    var dayofWeek = LocalDateTime.now().dayOfWeek.value
    var tempTasks = tasks
    //periodLocalTime(user.freeTime[0], user.freeTime[1])
    while(tempTasks.size != 0){
            var holiday = false
            for (j in user.dayoffNumber.indices) {
                if (dayofWeek == user.dayoffNumber[j]) {
                    holiday = true
                    break
                } else holiday = false
            }
            if(!holiday) {
                freeTime = periodLocalTime(user.freeTime[dayofWeek*2-1],user.freeTime[dayofWeek*2])
                for (n in tempTasks.indices) {
                    if (freeTime > taskDuration) {if(n+1<=tempTasks.size-1) taskDuration = sumLocalTime(taskDuration,localTimeToDouble(tempTasks[n + 1].durationTime)) else { taskDuration += 100.0 }} else {
                        for (x in 1..n) {
                            currentTasks = currentTasks.plus(tempTasks[x - 1])
                            tempTasks = tempTasks.drop(x - 1).toTypedArray()
                        }
                        break
                    }
                }
            }
            dayofWeek = if(dayofWeek + 1 > 7 ) 1 else dayofWeek + 1
            freeTime = 0.0
            taskDuration = localTimeToDouble(tempTasks[0].durationTime)
        }

    return currentTasks
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
    return abs(lastTimeDouble.toInt() - firstTimeDouble.toInt()) + minutes
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