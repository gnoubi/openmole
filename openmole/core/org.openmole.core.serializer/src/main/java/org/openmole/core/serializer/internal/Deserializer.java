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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import java.io.InputStream;
import org.openmole.commons.exception.InternalProcessingError;

/**
 *
 * @author reuillon
 */
public abstract class Deserializer implements ICleanable {

    final private XStream xstream = new XStream();

    public void registerConverter(SingleValueConverter converter) {
        xstream.registerConverter(converter);
    }

    <T> T fromXMLInjectFiles(InputStream is) throws InternalProcessingError {
        try {
            return (T) xstream.fromXML(is);
        } catch (XStreamException ex) {
            throw new InternalProcessingError(ex);
        }
    }
}
