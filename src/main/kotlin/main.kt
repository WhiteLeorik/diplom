import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours

data class User(val id: Int, var nickname: String, var freeTime: Array<LocalTime>, var dayoffNumber: Array<Int>, var holidays: Array<LocalDate>)

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

    fun calculationEndTime() {
        endTime = beginTime
        endTime = endTime.plusHours(durationTime.hour.toLong())
        endTime = endTime.plusMinutes(durationTime.minute.toLong())
    }

    override fun compareTo(other: Task): Int = this.priority.compareTo(other.priority)
}

fun main() {
    val george = User(
        1,
        "George",
        arrayOf(
            LocalTime.of(12, 30),
            LocalTime.of(17, 20),
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
        arrayOf(2, 7),
        arrayOf(
            LocalDate.of(2020,1,1),
            LocalDate.of(2020,1,2),
            LocalDate.of(2020,1,3),
            LocalDate.of(2020,1,4),
            LocalDate.of(2020,1,5),
            LocalDate.of(2020,1,6),
            LocalDate.of(2020,1,7),
            LocalDate.of(2020,1,8),
            LocalDate.of(2020,2,23),
            LocalDate.of(2020,3,8),
            LocalDate.of(2020,5,1),
            LocalDate.of(2020,5,9),
            LocalDate.of(2020,6,12),
            LocalDate.of(2020,11,4)
        )
    )

    val tasks = arrayOf(
        Task(2, "Имя", "Описание задачи", LocalTime.of(4, 0)),
        Task(1, "Имя2", "Описание задачи2", LocalTime.of(1, 30)),
        Task(3, "Имя3", "Описание задачи4", LocalTime.of(3, 10)),
        Task(10, "Имя4", "Описание задачи", LocalTime.of(1, 0)),
        Task(5, "Имя5", "Описание задачи", LocalTime.of(0, 30)),
        Task(10,"Imya","desk",LocalTime.of(10,30))
    )

    if (tasks.isNotEmpty()) {
        var currentTasks = taskAssignment(george, tasks)
        currentTasks.forEach { println(it) }
    }
}

fun taskAssignment(
    user: User, tasks: Array<Task>
): Array<Task> //Метод выборки задач, которые в приоритете и помещаются в свободное время пользователя
{
    tasks.sort()
    var plugTask = Task(1, "Plug", " ", LocalTime.of(0, 0))
    var currentTasks = arrayOf(plugTask)
    var freeTime = 0.0
    var dayofWeek = LocalDateTime.now().dayOfWeek.value
    var tempTasks = tasks
    var dayNow = true
    var currentDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0))
    while (tempTasks.isNotEmpty()) {
        var holiday = false
        for (j in user.dayoffNumber.indices) {
            if (dayofWeek == user.dayoffNumber[j]) {
                holiday = true
                dayNow = false
                break
            }
        }
        for (j in user.dayoffNumber.indices) {
            if (LocalDate.now().monthValue == user.holidays[j].monthValue && LocalDate.now().dayOfMonth == user.holidays[j].dayOfMonth) {
                holiday = true
                dayNow = false
                break
            }
        }
        if (!holiday) {
            freeTime = periodLocalTime(
                localTimeToDouble(user.freeTime[dayofWeek * 2 - 2]),
                localTimeToDouble(user.freeTime[dayofWeek * 2 - 1])
            )
            if(localTimeToDouble(LocalTime.now()) > localTimeToDouble(user.freeTime[dayofWeek * 2 - 2]) && dayNow) {
                freeTime = periodLocalTime(localTimeToDouble(LocalTime.now()),localTimeToDouble(user.freeTime[dayofWeek * 2 - 1]))
                !dayNow
            }
                tempTasks.forEach {
                if (freeTime > 0.0 && freeTime > localTimeToDouble(it.durationTime)) {
                    currentTasks = currentTasks.plus(it)
                    tempTasks = tempTasks.filterIndexed { index, _ -> index != tempTasks.indexOf(currentTasks.last()) }
                        .toTypedArray()
                    var durationTaskDouble =
                        periodLocalTime(freeTime, localTimeToDouble(user.freeTime[dayofWeek * 2 - 1]))
                    freeTime = periodLocalTimeNotAbs(localTimeToDouble(currentTasks.last().durationTime), freeTime)
                    currentTasks.last().beginTime = currentDate
                    currentTasks.last().beginTime = currentTasks.last().beginTime.plusHours(durationTaskDouble.toLong())
                    currentTasks.last().beginTime =
                        currentTasks.last().beginTime.plusMinutes((durationTaskDouble * 100).mod(100.0).toLong())
                    currentTasks.last().calculationEndTime()

                }
                if(periodLocalTime(
                        localTimeToDouble(user.freeTime[dayofWeek * 2 - 2]),
                        localTimeToDouble(user.freeTime[dayofWeek * 2 - 1])
                    ) < localTimeToDouble(it.durationTime)) {
                    tempTasks = tempTasks.filterIndexed { index, _ -> index != tempTasks.indexOf(it) }
                        .toTypedArray()
                    println("Задача ${it.name} больше, чем свободное время пользователя, её не вышло назначить")
                }
            }
            currentDate = currentDate.plusDays(1)
        }
        else { currentDate = currentDate.plusDays(1) }
        dayofWeek = if (dayofWeek + 1 > 7) 1 else dayofWeek + 1
        freeTime = 0.0

    }

    return currentTasks.copyOfRange(1, currentTasks.size)
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
    val secondTimeMinutes: Int = (firstDouble * 100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = abs(firstMinutes - secondMinutes)
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes / 100.0
}

fun periodLocalTimeNotAbs(firstDouble: Double, lastDouble: Double): Double {
    val hour: Int
    val minutes: Int
    val firstTimeHour: Int = lastDouble.toInt()
    val firstTimeMinutes: Int = (lastDouble * 100).mod(100.0).toInt()
    val firstMinutes = firstTimeHour * 60 + firstTimeMinutes
    val secondTimeHour: Int = firstDouble.toInt()
    val secondTimeMinutes: Int = (firstDouble * 100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = firstMinutes - secondMinutes
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes / 100.0
}

fun sumLocalTime(firstTime: Double, lastTime: Double): Double {
    val hour: Int
    val minutes: Int
    val firstTimeHour: Int = lastTime.toInt()
    val firstTimeMinutes: Int = (lastTime * 100).mod(100.0).toInt()
    val firstMinutes = firstTimeHour * 60 + firstTimeMinutes
    val secondTimeHour: Int = firstTime.toInt()
    val secondTimeMinutes: Int = (firstTime * 100).mod(100.0).toInt()
    val secondMinutes = secondTimeHour * 60 + secondTimeMinutes
    val differenceMinute = firstMinutes + secondMinutes
    hour = differenceMinute / 60
    minutes = differenceMinute % 60
    return hour + minutes / 100.0
}

//Сделать проверку на праздничные дни
//Сделать проверку на пересчет задач в середине рабочего времени