/**
 *
 */
package de.klassenserver7b.k7bot.util;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class VplanDBUtils {

    private static final Logger log;
    private static final List<String> defaultRooms;

    static {
        log = LoggerFactory.getLogger(VplanDBUtils.class);
        defaultRooms = List.of("005", "007", "013", "014", "019", "020", "203", "205", "214", "215", "220", "221");
    }

    /**
     * @param lesson the lesson to check
     * @param room   the room to check
     * @return if the room is free
     */
    public static boolean isRoomFree(long lesson, String room) {

        room = removeLeadingZero(room);

        try (ResultSet set = LiteSQL.onQuery("SELECT room FROM vplandata WHERE lesson=? ", lesson)) {

            boolean found = false;

            assert set != null;
            while (set.next()) {
                if (set.getString(1).equalsIgnoreCase(room)) {
                    found = true;
                    break;
                }
            }

            return !found;

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    public static List<String> checkDefaultRooms(long lesson) {
        List<String> ret = new ArrayList<>();

        for (String s : defaultRooms) {
            if (isRoomFree(lesson, s)) {
                ret.add(s);
            }
        }

        return ret;

    }

    public static HashMap<String, Long> getTeacherRooms(String teacher) {
        HashMap<String, Long> ret = new HashMap<>();

        try (ResultSet set = LiteSQL.onQuery("SELECT room, lesson FROM vplandata WHERE teacher=? ", teacher)) {
            assert set != null;
            while (set.next()) {
                ret.put(set.getString(1), set.getLong(2));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return ret;
    }

    public static String getTeacherRoomByLesson(String teacher, long lesson) {

        try (ResultSet set = LiteSQL.onQuery("SELECT room FROM vplandata WHERE teacher=? AND lesson=?", teacher, lesson)) {
            assert set != null;
            if (set.next()) {
                return set.getString(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String removeLeadingZero(String s) {
        if (s.startsWith("0")) {
            return s.substring(1);
        }
        return s;
    }

}
