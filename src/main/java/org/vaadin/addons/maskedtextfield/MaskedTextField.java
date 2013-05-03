package org.vaadin.addons.maskedtextfield;

import org.vaadin.addons.maskedtextfield.client.MaskedTextFieldState;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * Server side component for the VMaskedTextField widget.
 */
public class MaskedTextField extends TextField {

	private static final long serialVersionUID = 1L;

	public MaskedTextField() {
		super();
	}

	public MaskedTextField(String caption) {
		setCaption(caption);
	}

	public MaskedTextField(String caption, String mask) {
		setCaption(caption);
		setMask(mask);
	}

	public MaskedTextField(Property<String> dataSource) {
		super(dataSource);
	}

	public MaskedTextField(String caption, Property<String> dataSource) {
		super(caption, dataSource);
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

	@Override
	protected MaskedTextFieldState getState() {
		return (MaskedTextFieldState) super.getState();
	}
	
}