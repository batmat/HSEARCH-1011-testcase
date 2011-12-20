package net.batmat.hsearchissue;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;

@Embeddable
public class MyIndexedEntityId implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Field
	private String code;

	private Date date;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null)
		{
			return false;
		}
		if (!(o instanceof MyIndexedEntityId))
		{
			return false;
		}
		final MyIndexedEntityId MyIndexedEntityId = (MyIndexedEntityId)o;

		if (!code.equals(MyIndexedEntityId.getCode()))
		{
			return false;
		}
		if (!date.equals(MyIndexedEntityId.getDate()))
		{
			return false;
		}
		return true;
	}

	public int hashCode()
	{
		return code.hashCode() ^ date.hashCode();
	}
}
