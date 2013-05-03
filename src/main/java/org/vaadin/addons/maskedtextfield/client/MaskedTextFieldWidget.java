package org.vaadin.addons.maskedtextfield.client;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.vaadin.client.ui.VTextField;

public class MaskedTextFieldWidget extends VTextField implements KeyDownHandler,
		FocusHandler, BlurHandler, KeyPressHandler, ClickHandler {

	protected String mask;
	
	private char placeholder = '_';
	
	private StringBuilder string;
	private List<Mask> maskTest;
	private List<Integer> nullablePositions;
	
	private char emptyChar ='\0';
	
	/**
	 * Key press that might be ignored by event handlers
	 */
	private char[] ignoredKeys = new char[] {
			KeyCodes.KEY_BACKSPACE,
			KeyCodes.KEY_TAB,
			KeyCodes.KEY_DELETE,  
			KeyCodes.KEY_END,
			KeyCodes.KEY_ENTER,
			KeyCodes.KEY_ESCAPE,
			KeyCodes.KEY_HOME,
			KeyCodes.KEY_LEFT,
			KeyCodes.KEY_PAGEDOWN,
			KeyCodes.KEY_PAGEUP,
			KeyCodes.KEY_RIGHT
	};

	public MaskedTextFieldWidget() {
		super();
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addFocusHandler(this);
		addBlurHandler(this);
		//addClickHandler(this);
		Arrays.sort(ignoredKeys);
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
		nullablePositions = new ArrayList<Integer>();
		configureUserView();
		getNextPosition(-1);
	}
	
	public void setPlaceHolder(char placeHolder) {
		this.placeholder = placeHolder;
	}

	private void configureUserView() {
		for (int index = 0; index < mask.length(); index++) {
			char character = mask.charAt(index);
			createCorrectMaskAndPlaceholder(character, index);
		}
		setValue(string.toString());
	}

	private void createCorrectMaskAndPlaceholder(char character, int index) {
		switch (character) {
		case '\'':
			addMaskStrategyAndCharacterPlaceHolder(null, mask.charAt(++index));
			break;
		case '#':
			addMaskStrategyAndCharacterPlaceHolder(new NumericMask(),placeholder);
			break;
		case 'U':
			addMaskStrategyAndCharacterPlaceHolder(new UpperCaseMask(), placeholder);
			break;
		case 'L':
			addMaskStrategyAndCharacterPlaceHolder(new LowerCaseMask(), placeholder);
			break;
		case '?':
			addMaskStrategyAndCharacterPlaceHolder(new LetterMask(), placeholder);
			break;
		case 'A':
			addMaskStrategyAndCharacterPlaceHolder(new AlphanumericMask(), placeholder);
			break;
		case '*':
			addMaskStrategyAndCharacterPlaceHolder(new WildcardMask(), placeholder);
			break;
		case 'H':
			addMaskStrategyAndCharacterPlaceHolder(new HexMask(), placeholder);
			break;
		case '~':
			addMaskStrategyAndCharacterPlaceHolder(new SignMask(), placeholder);
			break;
		case '+':
			nullablePositions.add(index++);
			break;
		default:
			addMaskStrategyAndCharacterPlaceHolder(null, character);
			break;
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
				validateAndShowUserInput(event);
			} else {
				if(event.getCharCode() != emptyChar) {
					cancelKey();
				}
			}
		}
		setCursorPositionOnFirstPlaceHolder();
	}

	private boolean isKeyIgnored(KeyPressEvent event) {
		return (
				event.getCharCode() == emptyChar ||
				event.isShiftKeyDown() && isAnySelectionTextModifiedKey(event.getCharCode()) ||
				isIgnoredKey(event.getCharCode()) ||
				isPasteShorcutPressed(event) ||
				event.isAnyModifierKeyDown() && !event.isShiftKeyDown()
				);
	}
	
	private boolean isIgnoredKey(char charCode) {
		return Arrays.binarySearch(ignoredKeys, charCode) >= 0;
	}
	
	private boolean isAnySelectionTextModifiedKey(char charCode) {
		return (charCode == KeyCodes.KEY_END || charCode == KeyCodes.KEY_HOME);
	}
	
	private boolean isPasteShorcutPressed(KeyPressEvent event) {
		return event.isControlKeyDown() && (Character.toLowerCase(event.getCharCode()) == 'v');
	}

	private void validateAndShowUserInput(KeyPressEvent event) {
		Mask maskStrategy = maskTest.get(getCursorPos());
		if (maskStrategy != null) {
			if(event.getCharCode() == ' ' && nullablePositions.contains(getCursorPos())) {
				showUserInput(' ');
			}
			else if (maskStrategy.isValid(event.getCharCode())) {
				char character = maskStrategy.getChar(event.getCharCode());
				showUserInput(character);
			}
			event.preventDefault();
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
			deleteTextOnKeyDown(event);
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
			deleteTextOnKeyDown(event);
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
	
	private void deleteTextOnKeyDown(KeyDownEvent event) {
		if(!getSelectedText().isEmpty()) {
			String selected = getSelectedText();
			for(int i=(selected.length()-1); i>=0; i--) {
				int index = getText().indexOf(selected.charAt(i));
				deleteCharacter(index);
			}
			setCursorPositionOnFirstPlaceHolder();
		} else {
			if(event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
				deleteCharacterAndPositionCursor(event, getCursorPos());
			} else {
				deleteCharacterAndPositionCursor(event, getPreviousPosition(getCursorPos()));
			}
		}
	}

	private void deleteCharacterAndPositionCursor(KeyDownEvent event, int position) {
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
		}
		setCursorPositionOnFirstPlaceHolder();
	}
	
	private void setCursorPositionOnFirstPlaceHolder() {
		for(int i=0; i<mask.length(); i++) {
			char character = getValue().charAt(i);
			if(character == placeholder) {
				setCursorPos(i);
				break;
			}
		}
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

	@Override
	public void onClick(ClickEvent event) {
		setCursorPositionOnFirstPlaceHolder();
	}
}