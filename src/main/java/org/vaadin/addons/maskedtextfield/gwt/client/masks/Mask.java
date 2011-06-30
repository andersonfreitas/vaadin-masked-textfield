package org.vaadin.addons.maskedtextfield.gwt.client.masks;

public interface Mask {
	boolean isValid(char character);
	char getChar(char character);
}
