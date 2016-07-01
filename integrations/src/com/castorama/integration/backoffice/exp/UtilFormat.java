package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;

public class UtilFormat {

	private static final char[] blanks = new char[1000];

	private static final char[] zeros = new char[10];
	
	private static final String EXPORT_FILE_LINE_BREAK = ""; // in v3 sources "[\\m";
	
	private static final DecimalFormat moneyFormat = new DecimalFormat();
    
    static {
    	for (int i = 0; i < blanks.length; i++) {
    		blanks[i] = ' ';
    	}

    	for (int i = 0; i < zeros.length; i++) {
    		zeros[i] = '0';
    	}
    	
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(',');
		
		moneyFormat.setDecimalFormatSymbols(dfs);
		moneyFormat.setGroupingUsed(false);
		moneyFormat.setMinimumFractionDigits(2);
		moneyFormat.setMaximumFractionDigits(2);
		moneyFormat.setMinimumIntegerDigits(1);
    }

	public static StringBuffer fillEnd(StringBuffer sb, String value, int number, char character) {
		if (null == value) {
			value = "";
		}
		
		if (' ' == character) {
			return quickFillEnd(sb, value, number, blanks);
		}

		if ('0' == character) {
			return quickFillEnd(sb, value, number, zeros);
		}

		sb.append(value);
		
		int size = value.length();
		int toadd = number - size;
		while (toadd > 0) {
			sb.append(character);
			size ++;
			toadd = number - size;
		}
		
		return sb;
	}

	public static StringBuffer fillEnd200(StringBuffer sb, String value1, String value2) {
		if (null == value1) {
			value1 = "";
		}
		
		if (null == value2) {
			value2 = "";
		}

		String value;
		if (value1.length()+ value2.length() < 200) {
			value = value1 + " " + value2;
		} else {
			value = value1 + value2;
		}

		return quickFillEnd(sb, value, 200, blanks);
	}

	public static StringBuffer fillStart(StringBuffer sb, String value, int number, char character) {
		if (null == value) {
			value = "";
		}
		
		if (' ' == character) {
			return quickFillStart(sb, value, number, blanks);
		}

		if ('0' == character) {
			return quickFillStart(sb, value, number, zeros);
		}

		int size = value.length();
		int toadd = number - size;
		while (toadd > 0) {
			sb.append(character);
			size ++;
			toadd = number - size;
		}

		sb.append(value);
		
		return sb;
	}

	private static StringBuffer quickFillEnd(StringBuffer sb, String value, int number, char[] buffer) {
		sb.append(value);
		
		int size = value.length();
		int toadd = number - size;
		while (toadd > 0) {
			int bufSize = Math.min(toadd, buffer.length);
			
			sb.append(buffer, 0, bufSize);
			size += bufSize;
			toadd = number - size;
		}
		
		return sb;
	}

	private static StringBuffer quickFillStart(StringBuffer sb, String value, int number, char[] buffer) {
		int size = value.length();
		int toadd = number - size;
		while (toadd > 0) {
			int bufSize = Math.min(toadd, buffer.length);
			
			sb.append(buffer, 0, bufSize);
			size += bufSize;
			toadd = number - size;
		}
		sb.append(value);
		
		return sb;
	}
	
	public static String deleteCRLF(String source) {
		return unCrLf(source, EXPORT_FILE_LINE_BREAK);
	}
	
	public static String unCrLf(String source, String replace) {
		return source.replaceAll("\\r\\n", replace).replaceAll("\\r", replace).replace("\\n", replace);
	}

	public static String formatString(String value, int size, char charToComplete, boolean isValueStart) {
		if (value == null) {
			value = "";
		} else {
			value = value.replaceAll("<br/>", "");
			value = value.replaceAll("&agrave;", "à");
			value = value.replaceAll("&eacute;", "é");
		}
		value = deleteCRLF(value);
		
		StringBuilder sb = new StringBuilder(size);
		if (value.length() == size) {
			return value;
		} else if (value.length() > size) {
			return value.substring(0, size);
		} else {
			char[] arrayChars = new char[size - value.length()];
			Arrays.fill(arrayChars, charToComplete);
			if (isValueStart) {
				sb.append(value).append(arrayChars);
			} else {
				sb.append(arrayChars).append(value);
			}
		}
		return sb.toString();
	}
	
	public static String toMoneyString(double value) {
		return moneyFormat.format(value);
	}
	
	public static String valueToString(Object value) {
		return valueToString(value, "");
	}

	public static String valueToString(Object value, String nullStr) {
		if (null == value) {
			return nullStr;
		} else {
			return deleteCRLF(value.toString());
		}
	}
	
	public static String valueToString(Date date, DateFormat df) {
		return valueToString(date, df, "");
	}
		
	public static String valueToString(Date date, DateFormat df, String nullStr) {
		if (null == date) {
			return nullStr;
		}
		
		return df.format(date);
	}
	
	public static double valueToDouble(Number value, double nullValue) {
		if (null == value) {
			return nullValue;
		}
		
		return value.doubleValue();
	}

	public static double valueToDouble(Number value) {
		return valueToDouble(value, 0.0);
	}

	public static int valueToInt(Number value) {
		return valueToInt(value, 0);
	}

	public static int valueToInt(Number value, int nullValue) {
		if (null == value) {
			return nullValue;
		} else {
			return value.intValue();
		}
	}

	public static boolean valueToBoolean(Boolean propertyValue) {
		return valueToBoolean(propertyValue, false);
	}

	public static boolean valueToBoolean(Boolean propertyValue, boolean nullvalue) {
		return (null == propertyValue)?nullvalue:propertyValue.booleanValue();
	}

	public static boolean valueToBoolean(Number propertyValue, boolean nullvalue) {
		return (null == propertyValue)?nullvalue:(propertyValue.intValue() == 0);
	}

	public static boolean valueToBoolean(Number propertyValue) {
		return valueToBoolean(propertyValue, false);
	}

	public static long valueToLong(Number quantity, long nullValue) {
		return (null == quantity)?nullValue:quantity.longValue();
	}

	public static long valueToLong(Number quantity) {
		return valueToLong(quantity, 0);
	}

}
