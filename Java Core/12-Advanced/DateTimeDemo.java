// Problem  : Work with dates, times, durations and time zones correctly.
// Approach : Use the java.time API (Java 8+) - LocalDate/LocalTime/LocalDateTime, Instant, Duration,
//            Period, ZonedDateTime and formatting.
// Intuition: The old Date/Calendar classes were mutable, not thread-safe, and had genuinely
//            surprising semantics. java.time replaced them with IMMUTABLE, clearly-named types where
//            each type represents exactly one concept - so the compiler stops you mixing them up.
// Time     : O(1) for arithmetic   Space: O(1)
// Trade-off: More types to learn than a single Date, but each is unambiguous. Picking the right type
//            (does this need a time zone or not?) is most of the work, and getting it right prevents
//            an entire class of production bugs.

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeDemo {
    public static void main(String[] args) {

        // ---- Pick the type that matches the CONCEPT ----
        LocalDate date = LocalDate.of(2026, 8, 16);      // a date, no time, no zone (a birthday)
        LocalTime time = LocalTime.of(14, 30);           // a time of day (a shop opening at 09:00)
        LocalDateTime dt = LocalDateTime.of(date, time); // both, still no zone (a local appointment)
        Instant now = Instant.now();                     // a point on the UTC timeline (a log stamp)

        System.out.println("date    : " + date);         // 2026-08-16
        System.out.println("time    : " + time);         // 14:30
        System.out.println("dateTime: " + dt);           // 2026-08-16T14:30
        System.out.println("instant : " + now);          // ...Z (UTC)

        // ---- IMMUTABLE: every operation returns a NEW object ----
        LocalDate later = date.plusDays(10).plusMonths(1);
        System.out.println("original unchanged: " + date + "  derived: " + later);
        // This is why java.time is thread-safe by construction, unlike the old Calendar.

        // ---- Duration (time-based) vs Period (date-based) - they are NOT interchangeable ----
        Duration d = Duration.ofHours(3).plusMinutes(30);   // exact elapsed time: hours/min/sec
        Period p = Period.between(LocalDate.of(2026, 1, 1), date); // calendar amount: years/months/days
        System.out.println("duration: " + d.toMinutes() + " minutes");
        System.out.println("period  : " + p.getMonths() + " months, " + p.getDays() + " days");

        // Why both exist: "one month" is not a fixed number of hours (28-31 days), and "one day" is
        // not always 24 hours (daylight saving). Period respects the calendar; Duration counts seconds.

        System.out.println("days between: "
                + ChronoUnit.DAYS.between(LocalDate.of(2026, 1, 1), date));

        // ---- Time zones: only when the concept genuinely has one ----
        ZonedDateTime kolkata = dt.atZone(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime utc = kolkata.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("Kolkata: " + kolkata);
        System.out.println("same instant in UTC: " + utc);

        // ---- Comparison and queries ----
        System.out.println("isLeapYear 2026 : " + date.isLeapYear());          // false
        System.out.println("day of week     : " + date.getDayOfWeek());        // SUNDAY
        System.out.println("is before later : " + date.isBefore(later));       // true

        // ---- Formatting and parsing ----
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        System.out.println("formatted: " + date.format(fmt));                  // 16 Aug 2026
        System.out.println("parsed   : " + LocalDate.parse("2026-12-25"));     // ISO format by default
    }
}

/* ------------------------------ CHOOSING THE TYPE ------------------------------
 * Ask: does this concept have a time? does it have a zone?
 *
 *   LocalDate      date only, no zone        birthday, invoice date, due date
 *   LocalTime      time only, no zone        shop opens at 09:00 (in whatever local zone)
 *   LocalDateTime  date + time, NO zone      "meeting at 3pm on the 5th" as written on a wall
 *   ZonedDateTime  date + time + zone        an actual scheduled moment across regions
 *   Instant        a point on the UTC line   timestamps, logs, measuring elapsed time
 *   Duration       elapsed time (seconds)    timeouts, how long something took
 *   Period         calendar amount (Y/M/D)   age, subscription length
 *
 * RULE: store timestamps as Instant/UTC and convert to a zone only for DISPLAY. Storing local times
 * without a zone is the root cause of most date bugs - the value becomes ambiguous the moment it
 * crosses a boundary or a daylight-saving change.
 *
 * ------------------------------ WHY NOT Date/Calendar ---------------------------
 *   - MUTABLE, so a Date passed to a method could be changed underneath you.
 *   - NOT thread-safe (SimpleDateFormat especially - a classic source of corrupted output).
 *   - Surprising design: months were 0-based (January == 0) and years were offset from 1900.
 * They remain only for legacy compatibility; use java.time in all new code. Convert with
 * date.toInstant() and Date.from(instant) at the boundaries of old APIs.
 * -------------------------------------------------------------------------------- */
