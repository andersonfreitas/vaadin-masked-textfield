package org.vaadin.addons.maskedtextfield;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

public class NumericField extends TextField {

	private static final long serialVersionUID = 1L;

	public NumericField() {
		super();
	}

	/*
	public void setValue(Short number) {
		setNumberValue((Number) number);
	}
	
	public void setValue(Integer number) {
		setNumberValue((Number) number);
	}
	
	public void setValue(Long number) {
		setNumberValue((Number) number);
	}
	
	public void setValue(Double number) {
		setNumberValue(number);
	}
	
	public void setValue(Float number) {
		setNumberValue(number);
	}
	*/
	
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
	
	public NumericField(Property<Number> dataSource) {
		super(dataSource);
	}

	public NumericField(String caption, Property<Number> dataSource) {
		super(caption, dataSource);
	}

	public NumericField(String caption, String value) {
		super(caption, value);
	}

	public NumericField(String caption) {
		super(caption);
	}
}
