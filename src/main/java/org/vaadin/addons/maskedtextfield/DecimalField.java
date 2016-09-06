package org.vaadin.addons.maskedtextfield;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import org.vaadin.addons.maskedtextfield.client.DecimalFieldState;
import org.vaadin.addons.maskedtextfield.server.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.AbstractStringToNumberConverter;
import com.vaadin.ui.TextField;

public class DecimalField extends TextField {

	private static final long serialVersionUID = 1L;

	public DecimalField() {
		super();
		setConverter(new MaskNumberConverter());
	}

	public DecimalField(Property<?> dataSource) {
		super(dataSource);
		setConverter(new MaskNumberConverter());
	}

	public DecimalField(String caption, Property<?> dataSource) {
		super(caption, dataSource);
		setConverter(new MaskNumberConverter());
	}

	public DecimalField(String caption, String value) {
		super(caption, value);
		setConverter(new MaskNumberConverter());
	}

	public DecimalField(String caption) {
		super(caption);
		setConverter(new MaskNumberConverter());
	}
	
	public DecimalField(String mask, char decimalSeparator, char groupingSeparator) {
		this();
		setMask(mask);
		setDecimalSeparator(decimalSeparator);
		setGroupingSeparator(groupingSeparator);
	}
	
	@Override
	public void setValue(String string) {
		super.setValue(string);
	}
	
	public void setValue(Number number) {
		if(number != null) {
			if(getConverter() != null) {
				String v = getConverter().convertToPresentation(number, String.class, getLocale());
				setValue(v);
			} else {
				setValue( (String) null);
			}
		} else {
			setValue( (String) null);
		}
	}
	
	public void setMask(String mask) {
		if(mask == null) {
			throw new NullPointerException("The format mask cannot be null");
		}
		if(mask.trim().isEmpty()) {
			throw new IllegalStateException("The format mask cannot be empty");
		}
		getState().mask = mask;
	}
	
	public String getMask() {
		return getState().mask;
	}
	
	public void setDecimalSeparator(char decimalSeparator) {
		getState().decimalSeparator = decimalSeparator;
	}
	
	public char getDecimalSeparator() {
		return getState().decimalSeparator;
	}
	
	public void setGroupingSeparator(char groupingSeparator) {
		getState().groupingSeparator = groupingSeparator;
	}
	
	public char getGroupingSeparator() {
		return getState().groupingSeparator;
	}
	
	@Override
	public DecimalFieldState getState() {
		return (DecimalFieldState) super.getState();
	}
	
	public void setSelectValueOnFocus(boolean selectOnFocus) {
		getState().selectValuesOnFocus = selectOnFocus;
	}
	
	public boolean isSelectValueOnFocus() {
		return getState().selectValuesOnFocus;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		if(newDataSource != null) {
			if(!Number.class.isAssignableFrom(newDataSource.getType())) {
				throw new IllegalArgumentException("This field is compatible with number datasources only");
			}
			super.setPropertyDataSource(newDataSource);
		}
	}

	/**
	 * Custom converter to handle custom separators
	 * @author eduardo
	 *
	 */
	private class MaskNumberConverter extends AbstractStringToNumberConverter<Number> {

		private static final long serialVersionUID = 1L;

		private DecimalFormat formatter;
		
		public MaskNumberConverter() {
			refreshFormatter();
		}
		
		private void refreshFormatter() {
			if(formatter == null || 
					(	formatter.getDecimalFormatSymbols().getGroupingSeparator() != getGroupingSeparator()
					||  formatter.getDecimalFormatSymbols().getDecimalSeparator() != getDecimalSeparator()
					)
			) 
			{
				DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();
				decimalSymbols.setGroupingSeparator(getGroupingSeparator());
				decimalSymbols.setDecimalSeparator(getDecimalSeparator());
				formatter = new DecimalFormat();
				formatter.setDecimalFormatSymbols(decimalSymbols);
			}
		}
		
		
		@Override
		public Number convertToModel(String value, Class<? extends Number> targetType, Locale locale) throws ConversionException {
			refreshFormatter();
			try {
				if(value == null || value.trim().isEmpty()) {
					return null;
				}
				Number number = formatter.parse(value);
				if(getPropertyDataSource() != null) {
					return Utils.convertToDataSource(number, getPropertyDataSource());
				}
				return number;
			} catch (ParseException e) {
				return Utils.convertToDataSource(new Double(0.0), getPropertyDataSource());
			}
		}

		@Override
		public String convertToPresentation(Number value, Class<? extends String> targetType, Locale locale) throws ConversionException {
			if(value != null) {
				refreshFormatter();
				return formatter.format(value);
			}
			return null;
		}

		@Override
		public Class<Number> getModelType() {
			return Number.class;
		}

	}

}
