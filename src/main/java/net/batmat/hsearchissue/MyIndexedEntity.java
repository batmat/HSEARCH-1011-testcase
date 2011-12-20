package net.batmat.hsearchissue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;

@Indexed
@Entity
public class MyIndexedEntity
{
	/** Identifiant de l'objet. */
	@FieldBridge(impl = MyIndexedEntityIdFieldBridge.class)
	@Field
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	MyIndexedEntityId id;

	public MyIndexedEntityId getId()
	{
		return id;
	}

	public void setId(MyIndexedEntityId id)
	{
		this.id = id;
	}

	public String getSomeTitle()
	{
		return someTitle;
	}

	public void setSomeTitle(String someTitle)
	{
		this.someTitle = someTitle;
	}

	@Field
	String someTitle;
}
