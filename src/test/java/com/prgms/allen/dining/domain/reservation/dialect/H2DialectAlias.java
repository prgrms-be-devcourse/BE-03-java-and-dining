package com.prgms.allen.dining.domain.reservation.dialect;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class H2DialectAlias {
	public static String date_format(Date date, String mysqlFormatPattern) throws ParseException {
		if (date == null)
			return null;
		String dateFormatPattern = mysqlFormatPattern
			.replace("%Y", "yyyy")
			.replace("%m", "MM")
			.replace("%d", "dd");
		return date.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate().format(DateTimeFormatter.ofPattern(dateFormatPattern));
	}
}
