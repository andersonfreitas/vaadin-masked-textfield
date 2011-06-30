package org.vaadin.addons.maskedtextfield.gwt.client.masks;

public class NumericMask extends AbstractMask {
	
	public boolean isValid(char character) {
		return Character.isDigit(character);
	}

}
