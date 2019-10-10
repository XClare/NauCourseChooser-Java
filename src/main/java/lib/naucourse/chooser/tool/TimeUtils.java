package lib.naucourse.chooser.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 与时间相关的辅助方法
 */
public class TimeUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);

    /**
     * 获取字符串中的选课时间
     * 用于选课时间，选课开始与结束时间的转换
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public static Date getDateFromString(String dateStr) {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取字符串中的课程开课时间
     * 用于课程开课时间的转换
     *
     * @param timeStr 时间字符串
     * @return 时间
     */
    public static CourseTime getCourseTimeFromString(String timeStr) {
        CourseTime courseTime = new CourseTime();
        timeStr = timeStr.trim();
        if (timeStr.startsWith("周") && timeStr.contains("第") && timeStr.contains("-") && timeStr.endsWith("节")) {
            courseTime.setWeekDay(Integer.parseInt(timeStr.substring(1, 2)));
            timeStr = timeStr.substring(3, timeStr.length() - 1);
            int splitIndex = timeStr.indexOf("-");
            courseTime.setStartTime(Integer.parseInt(timeStr.substring(0, splitIndex)));
            courseTime.setEndTime(Integer.parseInt(timeStr.substring(splitIndex + 1)));
        }
        return courseTime;
    }

    public static class CourseTime {
        private int weekDay = 0;
        private int startTime = 0;
        private int endTime = 0;

        /**
         * 获取星期数
         * 1为周一，以此类推
         *
         * @return 星期数
         */
        public int getWeekDay() {
            return weekDay;
        }

        void setWeekDay(int weekDay) {
            this.weekDay = weekDay;
        }

        /**
         * 获取课程开始节次
         *
         * @return 课程开始节次
         */
        public int getStartTime() {
            return startTime;
        }

        void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        /**
         * 获取课程结束节次
         *
         * @return 课程结束节次
         */
        public int getEndTime() {
            return endTime;
        }

        void setEndTime(int endTime) {
            this.endTime = endTime;
        }
    }
}
