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
    //TODO("Если задача больше свободного времени, то цикл бесконечный исправить")
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
        Task(2, "Имя", "Описание задачи", LocalTime.of(4, 0)),
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
    }
}

fun taskAssignment(
    user: User, tasks: Array<Task>
): Array<Task> //Метод выборки задач, которые в приоритете и помещаются в свободное время пользователя
{
    var plugTask = Task(1, "Plug", " ", LocalTime.of(0, 0))
    var currentTasks = arrayOf(plugTask)
    var freeTime = 0.0
    var dayofWeek = LocalDateTime.now().dayOfWeek.value
    var tempTasks = tasks
    var x =0
    //TODO("К текущей дате не прибоавляется новый день исправить")
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
                tempTasks.forEach {
                    var tempTask = it
                    if(freeTime>0.0&&freeTime>localTimeToDouble(it.durationTime))  {
                        currentTasks = currentTasks.plus(tempTask)
                        tempTasks = tempTasks.filterIndexed{ index, _-> index !=tempTasks.indexOf(currentTasks.last())}.toTypedArray()
                        freeTime = periodLocalTimeNotAbs(localTimeToDouble(currentTasks.last().durationTime ),freeTime)
                        var durationTaskDouble = periodLocalTimeNotAbs(freeTime, localTimeToDouble(user.freeTime[dayofWeek*2-2]))
                        currentTasks.last().beginTime = currentDate
                        currentTasks.last().beginTime = currentTasks.last().beginTime.plusHours(durationTaskDouble.toLong())
                        currentTasks.last().beginTime = currentTasks.last().beginTime.plusMinutes((durationTaskDouble*100).mod(100.0).toLong())

                    }
                }
            }
            dayofWeek = if(dayofWeek + 1 > 7 ) 1 else dayofWeek + 1
            freeTime = 0.0
            x++
        }

    return currentTasks
}


fun localTimeToDouble(time: LocalTime): Double {
    return time.hour + (time.minute * 0.01)
}

fun periodLocalTime(firstDouble: Double, lastDouble: Double): Double {
    val hour: Int
    val minutes: Int
    val firstTimeHour: Int = lastDouble.toInt()
    val firstTimeMinutes: Int = (lastDouble * 100).mod(100.0).toInt()
    val firstMinutes = firstTimeHour * 60 + firstTimeMinutes
    val secondTimeHour: Int = firstDouble.toInt()
    val secondTimeMinutes: Int = (firstDouble*100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = abs(firstMinutes - secondMinutes)
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes/100.0
}
fun periodLocalTimeNotAbs(firstDouble: Double, lastDouble: Double): Double {
    val hour: Int
    val minutes: Int
    val firstTimeHour: Int = lastDouble.toInt()
    val firstTimeMinutes: Int = (lastDouble * 100).mod(100.0).toInt()
    val firstMinutes = firstTimeHour * 60 + firstTimeMinutes
    val secondTimeHour: Int = firstDouble.toInt()
    val secondTimeMinutes: Int = (firstDouble*100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = firstMinutes - secondMinutes
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes/100.0
}
fun sumLocalTime(firstTime: Double, lastTime: Double): Double {
    val hour: Int
    val minutes: Int
    val firstTimeHour: Int = lastTime.toInt()
    val firstTimeMinutes: Int = (lastTime * 100).mod(100.0).toInt()
    val firstMinutes = firstTimeHour * 60 + firstTimeMinutes
    val secondTimeHour: Int = firstTime.toInt()
    val secondTimeMinutes: Int = (firstTime*100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = firstMinutes + secondMinutes
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes/100.0
}

//Сделать проверку на выходные из времени интернета, а если попытка не удалась то из времени устройства

//Сделать проверку на праздничные дни
//Сделать проверку на пересчет задач в середине рабочего времени