package org.vaadin.addons.maskedtextfield.gwt.client.masks;
import org.vaadin.addons.maskedtextfield.gwt.client.masks.Mask;

public abstract class AbstractMask implements Mask {

	public char getChar(char character) {
		return character;
	}

}
