package org.vaadin.addons.maskedtextfield.client;

import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

public class MaskedTextFieldState extends AbstractTextFieldState {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The mask
	 */
	public String mask;
	
	/**
	 * A Placeholder
	 */
	public char placeHolder = '_';
	
}
