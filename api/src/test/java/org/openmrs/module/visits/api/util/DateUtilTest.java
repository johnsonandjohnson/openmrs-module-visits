package org.openmrs.module.visits.api.util;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.openmrs.module.visits.ContextMockedTest;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.visits.TestConstants.MILLISECONDS_PER_SECOND;
import static org.openmrs.module.visits.TestConstants.SECONDS_PER_MINUTE;

public class DateUtilTest extends ContextMockedTest {

    private static final double ONE_SECOND = 1;

    @Test
    public void getDatePlusSeconds() {
        Date date = new Date();
        Date datePlusSeconds = DateUtil.getDatePlusSeconds((long) SECONDS_PER_MINUTE);
        double l = (double) ((int) (datePlusSeconds.getTime() - date.getTime()) / MILLISECONDS_PER_SECOND);

        assertThat(l, Matchers.closeTo((double) SECONDS_PER_MINUTE, ONE_SECOND));
    }

    @Test
    public void isNotAfter() {
        Date date = new Date();
        Date dateAfter = new Date();
        assertTrue(DateUtil.isNotAfter(date, dateAfter));
    }

    @Test
    public void getDateSecondsAgo() {
        Date date = new Date();
        Date dateMinusSeconds = DateUtil.getDateSecondsAgo((long) SECONDS_PER_MINUTE);
        double l = (double) ((int) (date.getTime() - dateMinusSeconds.getTime()) / MILLISECONDS_PER_SECOND);

        assertThat(l, Matchers.closeTo((double) SECONDS_PER_MINUTE, ONE_SECOND));
    }

    @Test
    public void isSameInstant() {
        Date date = new Date();
        assertTrue(DateUtil.isSameInstant(date, date));
    }

    @Test
    public void convertServerSideDateFormatToFrontend() throws ParseException {
        String result = DateUtil.convertServerSideDateFormatToFrontend("2020-04-23");
        assertEquals("23 Apr 2020", result);
    }

    @Test
    public void convertFrontendDateFormatToServerSide() throws ParseException {
        String result = DateUtil.convertFrontendDateFormatToServerSide("23 Apr 2025");
        assertEquals("2025-04-23", result);
    }

    @Test
    public void toSimpleDate() {
        Date date = new Date();
        assertEquals(date, DateUtil.toSimpleDate(date));
    }
}
