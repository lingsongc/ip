package soar.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the conversion of supported scheduling text into calendar dates.
 */
public class DateTimeParserTest {
    private static final LocalDate EXPECTED_DATE = LocalDate.of(2019, 10, 15);

    /** Verifies that every documented date and date-time format is accepted. */
    @Test
    public void parseCalendarDate_supportedFormats_returnsCalendarDate() {
        Map<String, LocalDate> supportedInputs = Map.of(
                "2019-10-15", EXPECTED_DATE,
                "15/10/2019", EXPECTED_DATE,
                "15/10/2019 1800", EXPECTED_DATE,
                "2019-10-15 18:00", EXPECTED_DATE,
                "15 Oct 2019 6:00 PM", EXPECTED_DATE,
                "2019-10-15T18:00", EXPECTED_DATE);

        assertAll(supportedInputs.entrySet().stream()
                .map(entry -> () -> assertEquals(
                        entry.getValue(),
                        DateTimeParser.parseCalendarDate(entry.getKey()).orElseThrow(),
                        "Failed to parse supported input: " + entry.getKey())));
    }

    /** Verifies strict date validation while retaining valid leap days. */
    @Test
    public void parseCalendarDate_leapDay_returnsCalendarDate() {
        assertEquals(
                LocalDate.of(2020, 2, 29),
                DateTimeParser.parseCalendarDate("2020-02-29").orElseThrow());
    }

    /** Verifies that dates which do not exist are rejected. */
    @Test
    public void parseCalendarDate_impossibleDates_returnsEmpty() {
        assertAll(
                () -> assertTrue(DateTimeParser.parseCalendarDate("2019-02-29").isEmpty()),
                () -> assertTrue(DateTimeParser.parseCalendarDate("31/04/2019").isEmpty()),
                () -> assertTrue(DateTimeParser.parseCalendarDate("2019-13-01").isEmpty()));
    }

    /** Verifies that empty, malformed, and unsupported inputs are rejected. */
    @Test
    public void parseCalendarDate_unrecognizedInputs_returnsEmpty() {
        assertAll(
                () -> assertTrue(DateTimeParser.parseCalendarDate("").isEmpty()),
                () -> assertTrue(DateTimeParser.parseCalendarDate("not a date").isEmpty()),
                () -> assertTrue(DateTimeParser.parseCalendarDate("15-10-2019").isEmpty()),
                () -> assertTrue(DateTimeParser.parseCalendarDate(" 2019-10-15 ").isEmpty()));
    }
}
