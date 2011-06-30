package org.vaadin.addons.maskedtextfield.gwt.client.masks;

public class UpperCaseMask implements Mask {
	
	public boolean isValid(char character) {
		return Character.isLetter(getChar(character));
	}
	
	public char getChar(char character) {
		return Character.toUpperCase(character);
	}
}
