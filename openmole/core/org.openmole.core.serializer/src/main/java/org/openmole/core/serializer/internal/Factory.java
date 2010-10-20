/*
 * Copyright (C) 2010 reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.serializer.internal;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

/**
 *
 * @author reuillon
 */
public abstract class Factory<T extends ICleanable> {

   final ObjectPool pool = new SoftReferenceObjectPool(new BasePoolableObjectFactory() {

        @Override
        public Object makeObject() throws Exception {
            return Factory.this.makeObject();
        }

    });
    
    protected abstract T makeObject() throws Exception;
    
    T borrowObject() throws Exception {
        return (T) pool.borrowObject();
    }
    
    void returnObject(T serial) throws Exception {
        serial.clean();
        pool.returnObject(serial);
    }
    
}
