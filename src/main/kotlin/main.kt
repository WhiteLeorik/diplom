import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours

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
        Task(2, "Имя", "Описание задачи", LocalTime.of(5, 30)),
        Task(1, "Имя2", "Описание задачи2", LocalTime.of(1, 30)),
        Task(3, "Имя3", "Описание задачи4", LocalTime.of(3, 10)),
        Task(10, "Имя4", "Описание задачи", LocalTime.of(1, 0)),
        Task(5, "Имя5", "Описание задачи", LocalTime.of(0, 30))
    )

    if (tasks.isNotEmpty()) {
        tasks.sort()
        var currentTasks = taskAssignment(george,tasks)
        currentTasks = currentTasks.copyOfRange(1,currentTasks.size)
        for(i in currentTasks.indices) println(currentTasks[i])
        println(LocalDateTime.now().plusHours(10.3.toLong()))
    }
}

fun taskAssignment(
    user: User, tasks: Array<Task>
): Array<Task> //Метод выборки задач, которые в приоритете и помещаются в свободное время пользователя
{
    var plugTask = Task(1, "Plug", " ", LocalTime.of(0, 0))
    var currentTasks = arrayOf(plugTask)
    var freeTime = 0.0
    var taskDuration = 0.0
    var dayofWeek = LocalDateTime.now().dayOfWeek.value
    var tempTasks = tasks
    var x =0
    var currentDate = LocalDateTime.of(LocalDate.now(),LocalTime.of(0,0))
    //periodLocalTime(user.freeTime[0], user.freeTime[1])
    while(tempTasks.isNotEmpty()){
            var holiday = false
            for (j in user.dayoffNumber.indices) {
                if (dayofWeek == user.dayoffNumber[j]) {
                    holiday = true
                    break
                } else holiday = false
            }
            if(!holiday) {
                freeTime = periodLocalTime(localTimeToDouble(user.freeTime[dayofWeek*2-2]),localTimeToDouble(user.freeTime[dayofWeek*2-1]))
                taskDuration = localTimeToDouble(tempTasks[0].durationTime)
                for ( i in tempTasks.indices){
                    if(freeTime>0.0)  {
                        currentTasks = if(x==0) currentTasks.plus(tasks[i])
                        else {
                            currentTasks.plus(tasks[currentTasks.indexOf(currentTasks.last())])
                        }

                        tempTasks = tempTasks.filterIndexed{ index, _-> index !=tempTasks.indexOf(currentTasks.last())}.toTypedArray()
                        var durationTaskDouble = sumLocalTime(taskDuration, localTimeToDouble(user.freeTime[dayofWeek*2-2]))
                        currentTasks[i+1].beginTime = currentDate
                        currentTasks[i+1].beginTime = currentTasks[i+1].beginTime.plusHours(durationTaskDouble.toLong())
                        currentTasks[i+1].beginTime = currentTasks[i+1].beginTime.plusMinutes((durationTaskDouble*100).mod(100.0).toLong())
                        freeTime = sumLocalTime(-localTimeToDouble(currentTasks.last().durationTime ),freeTime)
                    }
                    if(i + 1 < tempTasks.size) taskDuration = sumLocalTime(taskDuration,localTimeToDouble(tempTasks[i+1].durationTime))
                }
            }
            dayofWeek = if(dayofWeek + 1 > 7 ) 1 else dayofWeek + 1
            freeTime = 0.0
            taskDuration = 0.0
            x++
        }

    return currentTasks
}


fun localTimeToDouble(time: LocalTime): Double {
    return time.hour + (time.minute * 0.01)
}

fun periodLocalTime(firstDouble: Double, lastDouble: Double): Double {
    var lastTimeDouble: Double = firstDouble
    var firstTimeDouble: Double = lastDouble
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