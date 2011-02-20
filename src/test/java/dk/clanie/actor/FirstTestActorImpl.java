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

import java.util.concurrent.Future;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Test actor.
 * 
 * @author Claus Nielsen
 */
@Actor
public class FirstTestActorImpl implements FirstTestActor {

	@Autowired private 
	SecondTestActor actor2;

	
	@Override
	public void voidMethod(int arg) {
		process("voidMethod", arg);
	}

	@Override
	public Future<?> methodReturningFuture(int arg) {
		process("methodReturningFuture", arg);
		return new AsyncResult<Object>(null);
	}

	@Override
	public Object methodReturningObject(int arg) {
		process("methodReturningObject", arg);
		return null;
	}

	@Override
	public Integer methodReturningObjectSubclass(int arg) {
		process("methodReturningObject", arg);
		return arg;
	}

	@Override
	public Future<?> methodCallingNextActor(int arg) {
		return actor2.methodCalledByFirstActor(arg);
	}

	@Override
	public Future<Boolean> methodReturningTrue() {
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	protected void process(String method, int arg) {
		Thread currentThread = Thread.currentThread();
		method = method(method);
		System.out.println(new Instant() + ": " + method + "(" + arg + ") START in " + currentThread);
		try {
			Thread.sleep(ActorAspectTest.SLEEPTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Instant() + ": " + method + "(" + arg + ") DONE in " + currentThread);
	}

	private String method(String method) {
		return getClass().getSimpleName()  + "." + method;
	}

	@Override
	public Object sync() {
		return null;
	}

}
