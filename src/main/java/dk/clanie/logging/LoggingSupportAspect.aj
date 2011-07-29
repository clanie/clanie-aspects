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
package dk.clanie.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Introduces a logger in objects "implementing" LoggingSupport.
 * 
 * @author Claus Nielsen
 */
public privileged aspect LoggingSupportAspect {

	private Logger LoggingSupport.log;

	before(LoggingSupport it) : initialization(LoggingSupport.new(..)) && target(it) {
		it.log = LoggerFactory.getLogger(it.getClass());
	}

	public Logger LoggingSupport.logger() {
		return log;
	}

}
