package org.vaadin.addons.maskedtextfield.client;

import org.vaadin.addons.maskedtextfield.DecimalField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(DecimalField.class)
public class DecimalFieldConnector extends TextFieldConnector {

	private static final long serialVersionUID = 1L;

	@Override
	protected Widget createWidget() {
		return GWT.create(DecimalFieldWidget.class);
	}
	
	@Override
	public DecimalFieldWidget getWidget() {
		return (DecimalFieldWidget) super.getWidget();
	}

	@Override
	public DecimalFieldState getState() {
		return (DecimalFieldState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		getWidget().setGroupingSeparator(getState().groupingSeparator);
		getWidget().setDecimalSeparator(getState().decimalSeparator);
		getWidget().setMask(getState().mask);
		super.onStateChanged(stateChangeEvent);
	}
	
}
