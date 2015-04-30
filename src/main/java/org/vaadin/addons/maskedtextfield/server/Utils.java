package org.vaadin.addons.maskedtextfield.server;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.vaadin.data.Property;

public class Utils {
	
	/**
	 * An utility method to convert some number to specific datasource final number implementation
	 * @param number
	 * @param property
	 * @return
	 */
	public static Number convertToDataSource(final Number number, final Property<?> property) {
		Class<?> propertyClass = property.getType();
		if(Integer.class.isAssignableFrom(propertyClass) || int.class.isAssignableFrom(propertyClass)) {
			return number.intValue();
		} else if(Long.class.isAssignableFrom(propertyClass) || long.class.isAssignableFrom(propertyClass)) {
			return number.longValue();
		} else if(Double.class.isAssignableFrom(propertyClass) || double.class.isAssignableFrom(propertyClass)) {
			return number.doubleValue();
		} else if(Float.class.isAssignableFrom(propertyClass) || float.class.isAssignableFrom(propertyClass)) {
			return number.floatValue();
		} else if(BigDecimal.class.isAssignableFrom(propertyClass)) {
			return new BigDecimal(number.doubleValue());
		} else if (BigInteger.class.isAssignableFrom(propertyClass)) {
			return new BigInteger(String.valueOf(number.longValue()));
		} else if(Short.class.isAssignableFrom(propertyClass) || short.class.isAssignableFrom(propertyClass)) {
			return number.shortValue();
		}
		return number;
	}

}
