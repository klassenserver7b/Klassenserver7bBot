/**
 * 
 */
package de.klassenserver7b.k7bot.util;

/**
 * @author K7
 *
 */
public enum LessonMapper {

	Lesson1("07:30", "08:15", 45),
	Lesson2("08:25", "09:10", 45),
	Lesson3("09:30", "10:15", 45),
	Lesson4("10:25", "11:10", 45),
	Lesson5("11:20", "12:05", 45),
	Lesson6("12:15", "13:00", 45),
	Lesson7("13:30", "14:15", 45),
	Lesson8("14:20", "15:05", 45),
	Lesson9("15:10", "15:55", 45),
	Lesson10("16:00", "16:45", 45),
	Lesson_Short1("07:30", "08:00", 30),
	Lesson_Short2("08:10", "08:40", 30),
	Lesson_Short3("09:00", "09:30", 30),
	Lesson_Short4("09:40", "10:10", 30),
	Lesson_Short5("10:20", "10:50", 30),
	Lesson_Short6("11:00", "11:30", 30),
	Lesson_Short7("12:00", "12:30", 30),
	Lesson_Short8("12:35", "13:05", 30),
	Lesson_Short9("13:10", "13:40", 30),
	Lesson_Short10("13:45", "14:15", 30);

	public final String start;
	public final String end;
	public final int duration;

	private LessonMapper(String start, String end, int dur) {
		this.start = start;
		this.end = end;
		this.duration = dur;
	}

}
