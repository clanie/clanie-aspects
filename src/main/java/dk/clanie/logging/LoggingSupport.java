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

/**
 * Marker interface for classes which need logging support.
 * 
 * Actual logging support in the form of a logger() method is introduced by the LoggingSupportAspect.
 * <p/>
 * A logger is added in each LoggingSupport instance, so LoggingSupport should not be used on classes
 * of which a huge number of instances will be created. Such classes should have a static logger field
 * instead, and since AspectJ cannot introduce static members on a interface you will have to add and
 * initialize that manually.
 * 
 * @author Claus Nielsen
 */
public interface LoggingSupport {

}
