package org.vaadin.addons.maskedtextfield;

import org.vaadin.addons.maskedtextfield.gwt.client.VNumericField;

import com.vaadin.data.Property;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.TextField;

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
