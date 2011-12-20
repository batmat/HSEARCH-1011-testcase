package net.batmat.hsearchissue;

import static org.fest.assertions.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class HSearchCompositeIdWithHsqldbIssueTest {
	@BeforeClass
	public static void setup() {
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:aname");
		cfg.setProperty("hibernate.connection.username", "sa");
		cfg.setProperty("hibernate.connection.password", "");

		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		cfg.setProperty("hibernate.current_session_context_class", "thread");
		cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");

		cfg.setProperty("hibernate.search.default.directory_provider",
				"org.hibernate.search.store.RAMDirectoryProvider");

		cfg.addAnnotatedClass(MyIndexedEntity.class);
		// cfg.setProperty("hibernate.search.default.indexBase", "target");
		sessionFactory = cfg.buildSessionFactory();
	}

	@Before
	public void buildSession() {
		if (session != null) {
			throw new IllegalArgumentException();
		}
		session = sessionFactory.openSession();
	}

	private static SessionFactory sessionFactory;
	private Session session;

	@Test
	public void demoIssue() throws Exception {
		index();

		List<MyIndexedEntity> list = session.createQuery("from MyIndexedEntity").list();
		assertThat(list).hasSize(3);
		for (MyIndexedEntity m : list) {
			System.out.println(m.getSomeTitle());
		}

		FullTextSession fullTextSession = Search.getFullTextSession(session);

		QueryParser queryParser = new MultiFieldQueryParser(new String[] { "someTitle" }, new StandardAnalyzer());

		Query query = queryParser.parse("temxt");

		Transaction transaction = session.getTransaction();
		transaction.begin();
		List<MyIndexedEntity> listFromIndex = fullTextSession.createFullTextQuery(query, MyIndexedEntity.class).list();
		transaction.commit();
		assertThat(listFromIndex).hasSize(1);
		MyIndexedEntity myEntity = listFromIndex.get(0);
		assertThat(myEntity).isNotNull();
		assertThat(myEntity.getSomeTitle()).isEqualTo(new String("pof, some new temxt, huhu"));
		assertThat(myEntity.getId().getCode()).isEqualTo("bafa222");

		// fullTextSession.close();

	}

	private void index() throws InterruptedException {
		List<MyIndexedEntity> entities = new ArrayList<MyIndexedEntity>();
		entities.add(createEntity("aafa010", "01/01/2011", "some text for aafa010"));
		entities.add(createEntity("aafa010", "01/01/2010", "some older text aafa010"));
		entities.add(createEntity("bafa222", "15/07/2009", "pof, some new temxt, huhu"));

		FullTextSession fullTextSession = Search.getFullTextSession(session);
		Transaction transaction = session.getTransaction();
		transaction.begin();
		for (MyIndexedEntity entity : entities) {
			session.persist(entity);
		}
		transaction.commit();

		Thread.sleep(1500);
	}

	private MyIndexedEntity createEntity(String code, String date, String text) {
		MyIndexedEntity entity = new MyIndexedEntity();
		MyIndexedEntityId id = new MyIndexedEntityId();
		id.setCode(code);
		id.setDate(format(date));
		entity.setId(id);
		entity.setSomeTitle(text);
		return entity;
	}

	private Date format(String date) {
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(formater.parse(date));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
