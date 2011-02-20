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

/**
 * Test actor interface.
 * 
 * @author cni
 */
public interface FirstTestActor {

	Object methodReturningObject(int arg);

	Future<?> methodReturningFuture(int arg);

	void voidMethod(int arg);

	Future<?> methodCallingNextActor(int arg);
	
	Integer methodReturningObjectSubclass(int arg);

	Future<Boolean> methodReturningTrue();

	/**
	 * Blocks caller until all previously submitted asynchronous calls completes.
	 * 
	 * @return
	 */
	Object sync();

}