package hu.kovacsa.szakdolgozat.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Data Generator application.
 * It adds data to an existing user_id;parent_id csv file.
 * The added data:
 * - date_of_birth
 * - last_year_income
 * - state
 *
 * Created by Aron Kovacs.
 */
public class DataGenerator {

	private static final String DEFAULT_FROM_BIRTH_DATE = "1950";
	private static final String DEFAULT_TO_BIRTH_DATE = "1997";

	private static final int MONEY_FROM = 90000;
	private static final int MONEY_TO = 3000000;

	private static final String ZERO = "0";
	private static final String EMPTY_STRING = "";
	private static final String ESCAPED_QUOTES = "\"";
	private static final String CSV_SEPARATOR = ",";
	private static final String END_OF_LINE = "\"\n";
	private static final String DATE_SEPARATOR = ".";

	/** Date pattern */
	private static final String DATE_FORMAT = "yyyy.mm.dd";

	/** csv file's first, header line */
	private static final String HEADER_LINE = "\"ID\",\"PARENT_ID\",\"DATE_OF_BIRTH\",\"LAST_YEAR_INCOME\",\"STATE\"";

	private static final String[] US_STATES = new String[] { "Alabama", "Alaska", "Arizona", "Arkansas", "Colorado",
		"Connecticut", "Delaware", "South Dakota", "South Carolina", "District of Columbia", "North Dakota",
		"North Carolina", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "California",
		"Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
		"Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New York",
		"West Virginia", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "Tennessee", "Texas",
		"Utah", "New Mexico", "Vermont", "Virginia", "Washington", "Wisconsin", "Wyoming" };

	public static void main(final String[] args) {
		if(args == null || args.length < 2){
			System.out.println("FROM and TO file names must be provided!");
		}
		final String fromFileName = args[0];
		final String toFileName = args[1];
		String fromDateStr = args[2];
		if(fromDateStr == null || fromDateStr.isEmpty()){
			fromDateStr = DEFAULT_FROM_BIRTH_DATE;
		}
		String toDateStr = args[3];
		if(toDateStr == null || toDateStr.isEmpty()){
			toDateStr = DEFAULT_TO_BIRTH_DATE;
		}

		generateData(fromFileName, toFileName, fromDateStr, toDateStr);
	}

	/**
	 * Returns a random number between:
	 * @param start
	 * 			and
	 * @param end
	 */
	private static int randomNumberBetween(final int start, final int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}

	/**
	 * The actual Data generating.
	 * @param fromFileName
	 * @param toFileName
	 */
	private static void generateData(final String fromFileName, final String toFileName, final String fromDateStr, final String toDateStr) {

		final File userDataFile = new File(fromFileName);
		final File outputFile = new File(toFileName);

		Integer fromDate = null;
		try {
			fromDate = Integer.parseInt(fromDateStr);
		} catch(final Exception e){
			System.out.println("Invalid FROM date, using default: " + DEFAULT_FROM_BIRTH_DATE);
		}

		Integer toDate = null;
		try {
			toDate = Integer.parseInt(toDateStr);
		} catch(final Exception e){
			System.out.println("Invalid TO date, using default: " + DEFAULT_TO_BIRTH_DATE);
		}

		try (final BufferedReader br = new BufferedReader(new FileReader(userDataFile));
				final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {

				if(first){
					writer.write(HEADER_LINE);
					first = false;
				}

				final int year = randomNumberBetween(fromDate, toDate);
				final int month = randomNumberBetween(1, 12);
				int day = randomNumberBetween(1, 31);
				if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
					day = randomNumberBetween(1, 30);
				} else if (month == 2) {
					day = randomNumberBetween(1, 28);
				}
				final String dateOfBirth = year + DATE_SEPARATOR + (month < 10 ? ZERO + month : month) + DATE_SEPARATOR
						+ (day < 10 ? ZERO + day : day);

				final String timeSinceEpoch = String.valueOf(new SimpleDateFormat(DATE_FORMAT).parse(dateOfBirth)
						.getTime());

				final int lastYearIncome = randomNumberBetween(MONEY_FROM, MONEY_TO);

				final String state = US_STATES[randomNumberBetween(0, US_STATES.length - 1)];

				final String[] data = line.split(CSV_SEPARATOR);

				writer.write(
						data[0].replaceAll(ESCAPED_QUOTES, EMPTY_STRING) + CSV_SEPARATOR +
						data[2].replaceAll(ESCAPED_QUOTES, EMPTY_STRING) + CSV_SEPARATOR +
						timeSinceEpoch + CSV_SEPARATOR +
						lastYearIncome + CSV_SEPARATOR + ESCAPED_QUOTES +
						state + END_OF_LINE
						);
			}
		} catch (final IOException | ParseException e) {
			System.out.println("Error creating data: " + e.getMessage());
			// Don't Panic, an error occured!
			System.exit(42);
		}
	}

}
