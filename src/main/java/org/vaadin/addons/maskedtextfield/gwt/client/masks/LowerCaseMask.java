package org.vaadin.addons.maskedtextfield.gwt.client.masks;

public class LowerCaseMask implements Mask {
	
	public boolean isValid(char character) {
		return Character.isLetter(getChar(character));
	}
	
	public char getChar(char character) {
		return Character.toLowerCase(character);
	}
}