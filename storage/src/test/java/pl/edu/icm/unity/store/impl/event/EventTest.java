/*
 * Copyright (c) 2016 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.store.impl.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pl.edu.icm.unity.base.event.Event;
import pl.edu.icm.unity.base.event.EventExecution;
import pl.edu.icm.unity.store.api.EventDAO;
import pl.edu.icm.unity.store.impl.AbstractBasicDAOTest;

public class EventTest extends AbstractBasicDAOTest<EventExecution>
{
	@Autowired
	private EventDAO dao;
	
	@Override
	protected EventDAO getDAO()
	{
		return dao;
	}

	@Override
	protected EventExecution getObject(String id)
	{
		Event event = new Event("category", 123l, new Date(1000), "contents");
		return new EventExecution(event, new Date(2000), "listenerId", 0);
	}

	@Override
	protected EventExecution mutateObject(EventExecution src)
	{
		Event event = new Event("category2", 1234l, new Date(3000), "contents2");
		EventExecution ret = new EventExecution(event, new Date(4000), "listenerId2", 1);
		ret.setId(src.getId());
		return ret;
	}

	@Test
	public void eventWithExecutionInPastIsReturnedByGetEligible()
	{
		tx.runInTransaction(() -> {
			EventExecution ev = getObject("");
			dao.create(ev);
			
			Date date = new Date(ev.getNextProcessing().getTime() + 1);
			List<EventExecution> eligible = dao.getEligibleForProcessing(date);
			
			assertThat(eligible.size(), is(1));
			assertThat(eligible.get(0), is(ev));
		});
	}
	
	@Test
	public void eventWithExecutionInFutureIsNotReturnedByGetEligible()
	{
		tx.runInTransaction(() -> {
			tx.runInTransaction(() -> {
				EventExecution ev = getObject("");
				dao.create(ev);
				
				Date date = new Date(ev.getNextProcessing().getTime() - 1);
				List<EventExecution> eligible = dao.getEligibleForProcessing(date);
				
				assertThat(eligible.isEmpty(), is(true));
			});
		});
	}

	@Test
	public void eventWithUpdatedExecutionIsReturned()
	{
		tx.runInTransaction(() -> {
			EventExecution ev = getObject("");
			long key = dao.create(ev);
			
			dao.updateExecution(key, new Date(2001), 123);
			
			EventExecution updated = dao.getByKey(key);
			
			assertThat(updated.getFailures(), is(123));
			assertThat(updated.getNextProcessing().getTime(), is(2001l));
		});
	}
	
	
	@Override
	public void importExportIsIdempotent()
	{
		//as of now events are not exported
	}
}
