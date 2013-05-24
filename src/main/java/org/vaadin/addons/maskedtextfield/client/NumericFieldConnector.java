package org.vaadin.addons.maskedtextfield.client;

import org.vaadin.addons.maskedtextfield.NumericField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(NumericField.class)
public class NumericFieldConnector extends TextFieldConnector {

	private static final long serialVersionUID = 1L;

	@Override
	protected Widget createWidget() {
		return GWT.create(NumericFieldWidget.class);
	}
	
	@Override
	public NumericFieldWidget getWidget() {
		return (NumericFieldWidget) super.getWidget();
	}
	
}
