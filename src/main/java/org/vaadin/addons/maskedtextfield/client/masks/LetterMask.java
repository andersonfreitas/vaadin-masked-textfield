package org.vaadin.addons.maskedtextfield.client.masks;

public class LetterMask extends AbstractMask {

	public boolean isValid(char character) {
		return Character.isLetter(character);
	}

}
