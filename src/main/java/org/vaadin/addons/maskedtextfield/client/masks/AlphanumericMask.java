package org.vaadin.addons.maskedtextfield.client.masks;

public class AlphanumericMask extends AbstractMask {
	
	public boolean isValid(char character) {
		return Character.isLetter(character) || Character.isDigit(character);
	}
}