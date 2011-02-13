/**
 * Copyright (C) 2011, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package dk.clanie.actor;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test ActorAspect.
 * 
 * @author Claus Nielsen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("actorAspectTestContext.xml")
public class ActorAspectTest {

	@Autowired private TestActor1 actor;
	@Autowired private TestActor2 actor2;
	
	private static final int ITERATIONS = 5;
	
	// Used in all test actor methods
	public static final int SLEEPTIME = 200;
	private static final double OVERHEAD = 0.5d;
	
	/**
	 * Tests that methods returning Future on two different actors are executed in parallel.
	 * <p/>
	 * Each method call is expected to complete after a little more than SLEEPIME ms.<br/>
	 * When calling methods on two different actors they should still complete almost as fast.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	@Timed(millis=(long)(ITERATIONS * SLEEPTIME * (1d + OVERHEAD)))
	public void testFutureParallel() throws InterruptedException, ExecutionException, TimeoutException {
		Future<?>[] futures = new Future<?>[ITERATIONS];
		Future<?>[] futures2 = new Future<?>[ITERATIONS];
		Instant startTime = new Instant();
		// Call actor methods
		for (int i = 0; i < ITERATIONS; i++) {
			futures[i] = actor.methodReturningFuture(i);
			futures2[i] = actor2.methodReturningFuture(i);
		}
		// Wait for them to complete
		for (int i = 0; i < ITERATIONS; i++) {
			futures[i].get(30, TimeUnit.SECONDS);
			futures2[i].get(30, TimeUnit.SECONDS);
		}
		// Check that they completed in the expected time.
		Instant endTime = new Instant();
		Duration executionTime = new Duration(startTime, endTime);
		Duration expectedExecutionTime = new Duration(ITERATIONS * SLEEPTIME);
		assertThat((double)executionTime.getMillis(), closeTo((double)expectedExecutionTime.getMillis(), expectedExecutionTime.getMillis() * OVERHEAD));
	}

	/**
	 * Tests that void methods on two different actors are executed in parallel.
	 * <p/>
	 * Each method call is expected to complete after a little more than SLEEPIME ms.<br/>
	 * When calling methods on two different actors they should still complete almost as fast.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	@Timed(millis=(long)(ITERATIONS * SLEEPTIME * (1d + OVERHEAD)))
	public void testVoidParallel() throws InterruptedException, ExecutionException, TimeoutException {
		Instant startTime = new Instant();
		// Call actor methods
		for (int i = 0; i < ITERATIONS; i++) {
			actor.voidMethod(i);
			actor2.voidMethod(i);
		}
		// Wait for them to complete
		actor.sync();
		actor2.sync();
		// Check that they completed in the expected time.
		Instant endTime = new Instant();
		Duration executionTime = new Duration(startTime, endTime);
		Duration expectedExecutionTime = new Duration(ITERATIONS * SLEEPTIME);
		assertThat((double)executionTime.getMillis(), closeTo((double)expectedExecutionTime.getMillis(), expectedExecutionTime.getMillis() * OVERHEAD));
	}


	/**
	 * Tests that methods returning something blocks the caller.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	@Timed(millis=(long)(ITERATIONS * SLEEPTIME * (1d + OVERHEAD) * 2))
	public void testSequential() throws InterruptedException, ExecutionException, TimeoutException {
		Instant startTime = new Instant();
		// Call actor methods
		for (int i = 0; i < ITERATIONS; i++) {
			actor.methodReturningObject(i);
			actor2.methodReturningObject(i);
		}
		// Check that they completed in the expected time.
		Instant endTime = new Instant();
		Duration executionTime = new Duration(startTime, endTime);
		Duration expectedExecutionTime = new Duration(ITERATIONS * SLEEPTIME * 2);
		assertThat((double)executionTime.getMillis(), closeTo((double)expectedExecutionTime.getMillis(), expectedExecutionTime.getMillis() * OVERHEAD));
	}

	/**
	 * Tests retrieving the return value from an synchronously called method.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testGettingReturnValue() throws InterruptedException, ExecutionException {
		Future<Boolean> future = actor.methodReturningTrue();
		assertThat(future.get(), equalTo(Boolean.TRUE));
	}

}
