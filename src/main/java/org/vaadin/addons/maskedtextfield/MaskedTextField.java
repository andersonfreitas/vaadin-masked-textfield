package org.vaadin.addons.maskedtextfield;

import java.util.Arrays;
import java.util.Locale;

import org.vaadin.addons.maskedtextfield.client.MaskedTextFieldState;
import org.vaadin.addons.maskedtextfield.server.Utils;
import org.vaadin.addons.maskedtextfield.shared.Constants;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToNumberConverter;
import com.vaadin.ui.TextField;

/**
 * Server side component for the VMaskedTextField widget.
 */
public class MaskedTextField extends TextField {

	private static final long serialVersionUID = 1L;
	
	private char digitRepresentation = '#';
	
	private Boolean maskClientOnly = false;
	
	public MaskedTextField() {
		super();
	}

	public MaskedTextField(String caption) {
		setCaption(caption);
		setConverter(new UnmaskModelConverter(this));
	}

	public MaskedTextField(String caption, String mask) {
		setCaption(caption);
		setMask(mask);
	}

	public MaskedTextField(Property<?> dataSource) {
		super(dataSource);
		setConverter(new UnmaskModelConverter(this));
	}

	public MaskedTextField(String caption, Property<?> dataSource) {
		super(caption, dataSource);
		setConverter(new UnmaskModelConverter(this));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		if(newDataSource != null) {
			super.setPropertyDataSource(newDataSource);
			if(Number.class.isAssignableFrom(newDataSource.getType())) {
				validateNumberPropertyWithMask();
				setConverter(new MaskNumberConverter());
			} else if (char.class.isAssignableFrom(newDataSource.getType()) || String.class.isAssignableFrom(newDataSource.getType())) {
				setConverter(new UnmaskModelConverter(this));
			}
		}
	}
	
	private void validateNumberPropertyWithMask() {
		char[] maskChars = getMask().replaceAll("\\+", "").toCharArray();
		for(char s : maskChars) {
			if(Arrays.binarySearch(Constants.MASK_REPRESENTATIONS, s) >= 0 && s != digitRepresentation) {
				throw new IllegalArgumentException("This mask is not compatible with numeric datasources");
			}
		}
	}

	public String getMask() {
		return getState().mask;
	}
	
	public void setMask(String mask) {
		getState().mask = mask;
	}
	
	public char getPlaceHolder() {
		return getState().placeHolder;
	}
	
	public void setPlaceHolder(char placeHolder) {
		getState().placeHolder = placeHolder;
	}
	
	public void setMaskClientOnly(boolean isMaskClientOnly) {
		this.maskClientOnly = isMaskClientOnly;
		setConverter(new UnmaskModelConverter(this));
	}
	
	public boolean isMaskClientOnly() {
		return maskClientOnly.booleanValue();
	}

	@Override
	protected MaskedTextFieldState getState() {
		return (MaskedTextFieldState) super.getState();
	}
	
	protected String unmask(final String value, String mask) {
		if(value == null || value.trim().isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder(value);
		mask = mask.replaceAll("\\+", "");
		int removedChars = 0;
		for(int i = 0; i<mask.length(); i++) {
			char s = mask.charAt(i);
			if(Arrays.binarySearch(Constants.MASK_REPRESENTATIONS, s) < 0) {
				if(i < value.length() && sb.charAt(i-removedChars) == s) {
					sb.deleteCharAt(i-removedChars);
					removedChars++;
				}
			}
		}
		return sb.toString();
	}
	
	protected String unmask(final String value) {
		return unmask(value, getMask());
	}
	
	/**
	 * Tenta converter uma instancia de mascaras unicamente de digitos para um numero aplicavel em um datasource
	 * @author Eduardo Frazao
	 *
	 */
	private class MaskNumberConverter extends StringToNumberConverter {

		private static final long serialVersionUID = 1L;
		
		@Override
		public Number convertToModel(String value, Class<? extends Number> targetType, Locale locale) throws ConversionException {
			String unmasked = unmask(value);
			if(unmasked != null) {
				try {
					Number n = super.convertToModel(unmasked, targetType, locale);
					return Utils.convertToDataSource(n, getPropertyDataSource());
				} catch (NumberFormatException ne) {
					return Utils.convertToDataSource(0, getPropertyDataSource());
				}
			}
			return Utils.convertToDataSource(0, getPropertyDataSource());
		}
		
	}
	
	/**
	 * Converter simples para remover a mascara caso configurado pelo usuario
	 * @author eduardo
	 *
	 */
	private static class UnmaskModelConverter implements Converter<String, String> {

		private static final long serialVersionUID = 1L;

		private MaskedTextField field;
		
		public UnmaskModelConverter(MaskedTextField field) {
			this.field = field;
		}
		@Override
		public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
			return value;
		}

		@Override
		public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
			if(field.isMaskClientOnly()) {
				String unmasked = field.unmask(value);
				if(unmasked != null) {
					return unmasked;
				}
			} 
			return value;
		}

		@Override
		public Class<String> getModelType() {
			return String.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
		
	}

}