package org.vaadin.addons.maskedtextfield.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.vaadin.addons.maskedtextfield.shared.Constants;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.vaadin.client.ui.VTextField;

public class NumericFieldWidget extends VTextField {
	
	private static Collection<Character> acceptedCharSet = new HashSet<Character>(Arrays.asList(
			new Character[] {
				(char) KeyCodes.KEY_BACKSPACE,
				(char) KeyCodes.KEY_TAB,
				(char) KeyCodes.KEY_DELETE,  
				(char) KeyCodes.KEY_END,
				(char) KeyCodes.KEY_ENTER,
				(char) KeyCodes.KEY_ESCAPE,
				(char) KeyCodes.KEY_HOME,
				(char) KeyCodes.KEY_LEFT,
				(char) KeyCodes.KEY_PAGEDOWN,
				(char) KeyCodes.KEY_PAGEUP,
				(char) KeyCodes.KEY_RIGHT
			}
			));
	
	private boolean isAcceptableKey(char charCode) {
		return Character.isDigit(charCode)
				|| acceptedCharSet.contains(charCode);
	}
	
	public NumericFieldWidget() {
		super();
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent e) {
				char code = (e.getCharCode() != Constants.EMPTY_CHAR) ? e.getCharCode() : (char) e.getNativeEvent().getKeyCode();
				if(!isAcceptableKey(code)) {
					e.preventDefault();
				}	
			}
		});
	}

	@SuppressWarnings("unused")
	private String getValueToOperate(String value) {
		return value.length() > 1 ? value.substring(value.length() - 2) : value
				.substring(value.length() - 1);
	}

	@SuppressWarnings("unused")
	private String getLiteralValue(String value) {
		return value.length() > 1 ? value.substring(0, value.length() - 2)
				: value.substring(0, value.length() - 1);
	}

	@Override
	public void setValue(String value) {
		try {
			Long v = Long.parseLong(value);
			super.setValue(v.toString());
		} catch (Throwable e) {
			super.setValue("0");
		}
	}
	
}
