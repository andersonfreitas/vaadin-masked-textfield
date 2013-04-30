package org.vaadin.addons.maskedtextfield.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addons.maskedtextfield.client.masks.AlphanumericMask;
import org.vaadin.addons.maskedtextfield.client.masks.HexMask;
import org.vaadin.addons.maskedtextfield.client.masks.LetterMask;
import org.vaadin.addons.maskedtextfield.client.masks.LowerCaseMask;
import org.vaadin.addons.maskedtextfield.client.masks.Mask;
import org.vaadin.addons.maskedtextfield.client.masks.NumericMask;
import org.vaadin.addons.maskedtextfield.client.masks.SignMask;
import org.vaadin.addons.maskedtextfield.client.masks.UpperCaseMask;
import org.vaadin.addons.maskedtextfield.client.masks.WildcardMask;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.vaadin.client.ui.VTextField;

public class MaskedTextFieldWidget extends VTextField implements KeyDownHandler,
		FocusHandler, BlurHandler, KeyPressHandler {

	protected String mask;
	private char placeholder = '_';
	private StringBuilder string;
	private List<Mask> maskTest;

	public MaskedTextFieldWidget() {
		super();
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addFocusHandler(this);
		addBlurHandler(this);
	}

	@Override
	public void setText(String value) {
		string = new StringBuilder(value);
		super.setText(value);
	}

	public void setMask(String mask) {
		this.mask = mask;
		string = new StringBuilder();
		maskTest = new ArrayList<Mask>();
		configureUserView();
		getNextPosition(-1);
	}

	private void configureUserView() {
		for (int index = 0; index < mask.length(); index++) {
			char character = mask.charAt(index);
			createCorrectMaskAndPlaceholder(character, index);
		}
		setValue(string.toString());
	}

	private void createCorrectMaskAndPlaceholder(char character, int index) {
		if (character == '\'') {
			addMaskStrategyAndCharacterPlaceHolder(null, mask.charAt(++index));
		} else if (character == '#') {
			addMaskStrategyAndCharacterPlaceHolder(new NumericMask(),
					placeholder);
		} else if (character == 'U') {
			addMaskStrategyAndCharacterPlaceHolder(new UpperCaseMask(),
					placeholder);
		} else if (character == 'L') {
			addMaskStrategyAndCharacterPlaceHolder(new LowerCaseMask(),
					placeholder);
		} else if (character == '?') {
			addMaskStrategyAndCharacterPlaceHolder(new LetterMask(),
					placeholder);
		} else if (character == 'A') {
			addMaskStrategyAndCharacterPlaceHolder(new AlphanumericMask(),
					placeholder);
		} else if (character == '*') {
			addMaskStrategyAndCharacterPlaceHolder(new WildcardMask(),
					placeholder);
		} else if (character == 'H') {
			addMaskStrategyAndCharacterPlaceHolder(new HexMask(), placeholder);
		} else if (character == '~') {
			addMaskStrategyAndCharacterPlaceHolder(new SignMask(), placeholder);
		} else {
			addMaskStrategyAndCharacterPlaceHolder(null, character);
		}
	}

	private void addMaskStrategyAndCharacterPlaceHolder(Mask maskStrategy,
			char characterPlaceholder) {
		maskTest.add(maskStrategy);
		string.append(characterPlaceholder);
	}

	private int getNextPosition(int position) {
		while (++position < maskTest.size() && maskTest.get(position) == null)
			;
		return position;
	}

	int getPreviousPosition(int position) {
		while (--position >= 0 && maskTest.get(position) == null)
			;
		if (position < 0)
			return getNextPosition(position);
		return position;
	}

	private int getLastPosition() {
		return getValue().length() + 1;
	}

	public void onKeyPress(KeyPressEvent event) {
		if (!isKeyIgnored(event)) {
			if (getCursorPos() < maskTest.size()) {
				validateAndShoywUserInput(event);
			}
		}
	}

	private boolean isKeyIgnored(KeyPressEvent event) {
		return event.getCharCode() == KeyCodes.KEY_BACKSPACE
				|| event.getCharCode() == KeyCodes.KEY_TAB
				|| event.getCharCode() == KeyCodes.KEY_DELETE
				|| event.getCharCode() == KeyCodes.KEY_END
				|| event.getCharCode() == KeyCodes.KEY_ENTER
				|| event.getCharCode() == KeyCodes.KEY_ESCAPE
				|| event.getCharCode() == KeyCodes.KEY_HOME
				|| event.getCharCode() == KeyCodes.KEY_LEFT
				|| event.getCharCode() == KeyCodes.KEY_PAGEDOWN
				|| event.getCharCode() == KeyCodes.KEY_PAGEUP
				|| event.getCharCode() == KeyCodes.KEY_RIGHT
				|| event.isAnyModifierKeyDown();
	}

	private void validateAndShoywUserInput(KeyPressEvent event) {
		Mask maskStrategy = maskTest.get(getCursorPos());
		if (maskStrategy != null) {
			if (maskStrategy.isValid(event.getCharCode())) {
				char character = maskStrategy.getChar(event.getCharCode());
				showUserInput(character);
				event.preventDefault();
			}
		} else {
			setCursorPos(getNextPosition(getCursorPos()));
		}		
	}

	private void showUserInput(char character) {
		int currentPosition = getCursorPos();
		string.setCharAt(currentPosition, character);
		setValue(string.toString());
		setCursorPos(getNextPosition(currentPosition));
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
			deleteCharacterAndPositionCursor(event,
					getPreviousPosition(getCursorPos()));
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
			deleteCharacterAndPositionCursor(event, getCursorPos());
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
			setCursorPositionAndPreventDefault(event,getNextPosition(getCursorPos()));
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
			setCursorPositionAndPreventDefault(event, getPreviousPosition(getCursorPos()));
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_HOME
				|| event.getNativeKeyCode() == KeyCodes.KEY_UP) {
			setCursorPositionAndPreventDefault(event, getPreviousPosition(0));
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_END
				|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
			setCursorPositionAndPreventDefault(event, getLastPosition());
		} else {
			super.onKeyDown(event);
		}
	}

	private void deleteCharacterAndPositionCursor(KeyDownEvent event,
			int position) {
		deleteCharacter(position);
		setCursorPositionAndPreventDefault(event, position);
	}

	private void setCursorPositionAndPreventDefault(KeyDownEvent event,int position) {
		setCursorPos(position);
		event.preventDefault();
	}

	private void deleteCharacter(int position) {
		Mask maskStrategy = maskTest.get(position);
		if (maskStrategy != null) {
			string.setCharAt(position, placeholder);
			setValue(string.toString());
		}
	}

	public void onFocus(FocusEvent event) {
		if (getValue().isEmpty()) {
			setMask(mask);
		} else
			setCursorPos(getPreviousPosition(0));
	}

	public void onBlur(BlurEvent event) {
		for (int index = 0; index < string.length(); index++) {
			char character = string.charAt(index);
			if (maskTest.get(index) != null && character == placeholder) {
				setValue("");
				valueChange(true);
				return;
			}
		}
	}
}