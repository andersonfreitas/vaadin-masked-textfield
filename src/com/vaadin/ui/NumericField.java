package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VNumericField;

@ClientWidget(VNumericField.class)
public class NumericField extends TextField {
	private static final long serialVersionUID = 3119804051599796474L;

	public NumericField() {
		super();
	}

	public NumericField(Property dataSource) {
		super(dataSource);
	}

	public NumericField(String caption, Property dataSource) {
		super(caption, dataSource);
	}

	public NumericField(String caption, String value) {
		super(caption, value);
	}

	public NumericField(String caption) {
		super(caption);
	}
}
