package net.batmat.hsearchissue;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.search.bridge.TwoWayStringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyIndexedEntityIdFieldBridge implements TwoWayStringBridge
{
	protected static final char SEPARATOR = ' ';

	private static final Logger LOGGER = LoggerFactory.getLogger(MyIndexedEntityIdFieldBridge.class);

	public Object stringToObject(String stringValue)
	{
		assert stringValue != null;
		int separatorIndex = stringValue.indexOf(SEPARATOR);
		String codeActe = stringValue.substring(0, separatorIndex);
		String theDateString = stringValue.substring(separatorIndex + 1, stringValue.length());

		Calendar c = getInstance();
		c.set(YEAR, Integer.parseInt(theDateString.substring(0, 4)));
		c.set(MONTH, Integer.parseInt(theDateString.substring(4, 6)) - 1);
		c.set(DAY_OF_MONTH, Integer.parseInt(theDateString.substring(6, 8)));
		c.set(HOUR_OF_DAY, 0);
		c.set(MINUTE, 0);
		c.set(SECOND, 0);
		c.set(MILLISECOND, 0);

		Date theDate = c.getTime();

		MyIndexedEntityId id = new MyIndexedEntityId();
		id.setCode(codeActe);
		id.setDate(theDate);
		return id;
	}

	public String objectToString(Object object)
	{
		assert object != null;
		MyIndexedEntityId id = (MyIndexedEntityId)object;
		Date date = id.getDate();
		if (dateHasNotNullHour(date))
		{
			LOGGER
				.warn(
					"The date {} with code {} has an issue with its time part. Shouldn't be different from 0.",
					id.getDate(), id.getCode());
		}
		String dateToString = new SimpleDateFormat("yyyyMMdd").format(date);
		return id.getCode() + SEPARATOR + dateToString;
	}

	private boolean dateHasNotNullHour(Date dateModif)
	{
		Calendar c = getInstance();
		c.setTime(dateModif);
		return !(c.get(HOUR_OF_DAY) == 0 && c.get(MINUTE) == 0 && c.get(SECOND) == 0 && c.get(MILLISECOND) == 0);
	}
}
