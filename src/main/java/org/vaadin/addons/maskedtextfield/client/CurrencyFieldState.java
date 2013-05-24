package org.vaadin.addons.maskedtextfield.client;

import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

public class CurrencyFieldState extends AbstractTextFieldState {

	private static final long serialVersionUID = 1L;
	
	public char decimalSeparator = '.';
	
	public char groupingSeparator = ',';
	
	public String mask = "#.00";

}
