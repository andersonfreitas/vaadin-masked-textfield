package org.vaadin.addons.maskedtextfield.client;

import java.util.HashMap;
import java.util.Map;

public class PrefixedMaskedTextFieldState extends MaskedTextFieldState {

	private static final long serialVersionUID = 1L;

	public String defaultMask;
	
	public Map<String, String> masksByPrefix = new HashMap<String, String>();
	
}
