package net.batmat.hsearchissue;



import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class MyIndexedEntityFieldBridgeTest
{
	@Test
	public void checkToString() {
		MyIndexedEntityId id = new MyIndexedEntityId();
		id.setCode("CODEACTE11");
		id.setDate(new Date(111, Calendar.JANUARY, 21));
		MyIndexedEntityIdFieldBridge bridge = new MyIndexedEntityIdFieldBridge();

		String representationString = bridge.objectToString(id);

		System.out.println(representationString);
		assertThat(representationString).startsWith("CODEACTE11");
		assertThat(representationString).endsWith("20110121");
	}
	
	@Test
	public void checkToDate()
	{
		String stringRepresentation = "ABC" + MyIndexedEntityIdFieldBridge.SEPARATOR + "20110325";
		MyIndexedEntityIdFieldBridge bridge = new MyIndexedEntityIdFieldBridge();
		MyIndexedEntityId id = (MyIndexedEntityId) bridge.stringToObject(stringRepresentation);
		
		assertThat(id.getCode()).isEqualTo("ABC");
		Date date = id.getDate();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		assertThat(c.get(Calendar.YEAR)).isEqualTo(2011);
		assertThat(c.get(Calendar.MONTH)).isEqualTo(Calendar.MARCH);
		assertThat(c.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
	}
}
