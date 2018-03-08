package org.vaadin.addons.maskedtextfield.client;

import org.vaadin.addons.maskedtextfield.PrefixedMaskedTextField;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;

@Connect(PrefixedMaskedTextField.class)
public class PrefixedMaskedTextFieldConnector extends MaskedTextFieldConnector {

	private static final long serialVersionUID = 1L;

	@Override
	protected PrefixedMaskedTextFieldWidget createWidget() {
		return GWT.create(PrefixedMaskedTextFieldWidget.class);
	}
	
	@Override
	public PrefixedMaskedTextFieldWidget getWidget() {
		return (PrefixedMaskedTextFieldWidget) super.getWidget();
	}

	@Override
	public PrefixedMaskedTextFieldState getState() {
		return (PrefixedMaskedTextFieldState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		getWidget().setMasks(getState().masksByPrefix);
		getWidget().setDefaultMask(getState().defaultMask);
	}
	
}
