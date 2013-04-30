package org.vaadin.addons.maskedtextfield;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

public class NumericField extends TextField {

	private static final long serialVersionUID = 1L;

	public NumericField() {
		super();
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
