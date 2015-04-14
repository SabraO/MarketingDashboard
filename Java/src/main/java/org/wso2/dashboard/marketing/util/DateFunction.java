package org.wso2.dashboard.marketing.util;

import org.wso2.dashboard.marketing.model.DateRange;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateFunction {

	private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
	private static final String DATE_SEPARATOR = "-";
	private static final String DATE_FORMAT = "yyyy" + DATE_SEPARATOR + "MM" + DATE_SEPARATOR + "dd";
	private static final String FIRST_QUARTER_END_DATE = "03" + DATE_SEPARATOR + "31";
	private static final String SECOND_QUARTER_END_DATE = "06" + DATE_SEPARATOR + "30";
	private static final String THIRD_QUARTER_END_DATE = "09" + DATE_SEPARATOR + "30";
	private static final String FOURTH_QUARTER_END_DATE = "12" + DATE_SEPARATOR + "31";

	public static int getQNumIfDateRangeContainsEOQ(String startDateString, String endDateString)
			throws ParseException {

		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);

		//Check for all the quarter end dates
		String[] quarterEndDates =
				{ FIRST_QUARTER_END_DATE, SECOND_QUARTER_END_DATE, THIRD_QUARTER_END_DATE, FOURTH_QUARTER_END_DATE };

		String quarterEndDateString = Integer.toString(year) + DATE_SEPARATOR, eoqDateString;
		Date startDate = sdfDate.parse(startDateString), endDate = sdfDate.parse(endDateString), quarterDate;

		for (int i = 0; i < quarterEndDates.length; i++) {

			eoqDateString = quarterEndDates[i];
			quarterEndDateString += eoqDateString;
			quarterDate = sdfDate.parse(quarterEndDateString);

			if (quarterDate.after(startDate) && quarterDate.before(endDate)) {
				return i + 1;
			}
		}

		return -1;
	}

	public static List<DateRange> getQuarterlyDateRange(String startDateString, String endDateString, int quarterNum)
			throws ParseException {

		List<DateRange> dateRangesList = new ArrayList<DateRange>();

		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String quarterEndDateString = Integer.toString(year);

		switch (quarterNum) {
			case 1:
				quarterEndDateString += DATE_SEPARATOR + FIRST_QUARTER_END_DATE;
				break;
			case 2:
				quarterEndDateString += DATE_SEPARATOR + SECOND_QUARTER_END_DATE;
				break;
			case 3:
				quarterEndDateString += DATE_SEPARATOR + THIRD_QUARTER_END_DATE;
				break;
			case 4:
				quarterEndDateString += DATE_SEPARATOR + FOURTH_QUARTER_END_DATE;
				break;
		}

		Date quarterDate = sdfDate.parse(quarterEndDateString);
		Date dateAfterQuarter = new Date(quarterDate.getTime() + DAY_IN_MS);

		addDateRange(startDateString, dateRangesList, sdfDate, quarterDate);
		addDateRange(endDateString, dateRangesList, sdfDate, dateAfterQuarter);

		return dateRangesList;
	}

	private static void addDateRange(String dateString, List<DateRange> dateRangesList, SimpleDateFormat sdfDate,
	                                 Date date) {
		DateRange firstDateRange = new DateRange();
		firstDateRange.setStartDate(dateString);
		firstDateRange.setEndDate(sdfDate.format(date));
		dateRangesList.add(firstDateRange);
	}

	public static DateRange getWeeklyDateRange() {

		DateRange weeklyDateRange = new DateRange();

		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);

		//Get first date in date range
		Date firstDate = new Date(System.currentTimeMillis() - (8 * DAY_IN_MS));
		weeklyDateRange.setStartDate(sdfDate.format(firstDate));

		//Get last date in date range
		Date lastDate = new Date(System.currentTimeMillis() - (2 * DAY_IN_MS));
		weeklyDateRange.setEndDate(sdfDate.format(lastDate));

		return weeklyDateRange;
	}

}
