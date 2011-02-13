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

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dk.clanie.actor.Actor;

/**
 * Advice classes annotated with @Actor.
 * </p>
 * Submits all (external) calls to actor methods for asynchronous and sequential execution.
 * 
 * @author Claus Nielsen
 */
privileged aspect ActorAspect perthis(constructor()) {

	pointcut constructor() : execution ((@Actor *).new(..));

	pointcut voidMethod() : execution (!private (void) (@Actor *).*(..));
	pointcut asyncMethod() : execution (!private (Future) (@Actor *).*(..));
	pointcut blockingMethod() : execution (!private !(void || Future) (@Actor *).*(..));

	// Add @Component on Actors
	declare @type: (@Actor !@Component Object+): @Component;

	// State
	private ThreadPoolTaskExecutor executor;
	private Thread myThread;

	/**
	 * Initialize.
	 */
	after(Object actor) : constructor() && this(actor) {
		executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(1);
		executor.setDaemon(true);
		executor.setThreadNamePrefix(actor.getClass().getSimpleName() + ",");
		executor.initialize();
		Future<Thread> future = executor.submit(new Callable<Thread>() {
			@Override
			public Thread call() throws Exception {
				return Thread.currentThread();
			}
		});
		try {
			myThread = future.get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Acttor initialization failed.", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Acttor initialization failed.", e);
		}
	}

	/**
	 * Submits methods for asynchronous execution in the actor's thread.
	 * <p/>
	 * This is used on void methods.<br/>
	 * 
	 * @return Future.
	 */
	@SuppressWarnings("all")
	Object around() : voidMethod() {
		if (Thread.currentThread() == myThread) {
			return proceed();
		} else {
			Future future = executor.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					return proceed();
				}
			});
			return future;
		}
	}

	/**
	 * Submits methods for asynchronous execution in the actor's thread.
	 * <p/>
	 * This is used on methods returning a Future.<br/>
	 * 
	 * @return Future.
	 */
	@SuppressWarnings("all")
	Future around() : asyncMethod() {
		if (Thread.currentThread() == myThread) {
			return proceed();
		} else {
			Future<?> future = executor.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					return proceed().get();
				}
			});
			return future;
		}
	}

	/**
	 * Submits methods for asynchronous execution in the actor's thread.
	 * <p/>
	 * This is used on methods returning something other than Future or void.
	 * The method is executed in the actor's thread, but the caller is blocked,
	 * and then the method completes the result is returned to the caller.
	 * 
	 * @return Future.
	 */
	@SuppressWarnings("all")
	Object around() : blockingMethod() {
		if (Thread.currentThread() == myThread) {
			return proceed();
		} else {
			Future future = executor.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					return proceed();
				}
			});
			try {
				return future.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
