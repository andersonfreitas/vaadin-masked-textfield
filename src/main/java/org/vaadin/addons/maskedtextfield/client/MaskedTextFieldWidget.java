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
import org.vaadin.addons.maskedtextfield.shared.Constants;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VTextField;

public class MaskedTextFieldWidget extends VTextField implements KeyDownHandler,
		FocusHandler, BlurHandler, KeyPressHandler {

	protected String mask;
	private String proccessedMask;
	
	private char placeholder = '_';
	
	private StringBuilder string;
	private List<Mask> maskTest;
	private List<Integer> nullablePositions;
	
	private char emptyChar ='\0';
	
	private boolean immediate = false;
	
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
		Arrays.sort(ignoredKeys);
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addFocusHandler(this);
		addBlurHandler(this);
		sinkEvents(Event.ONPASTE);
	}

	@Override
	public void setText(String value) {
		setText(value, true);
	}
	
	protected void setText(String value, boolean checkComplete) {
		String v = formatString(value);
		string = new StringBuilder(v);
		super.setText((checkComplete && isFieldIfIncomplete()) ? "" : v);
		valueChange(false);
	}

	public void setMask(String mask) {
		setMask(mask, true);
	}
	
	protected void setMask(String mask, boolean replaceValue) {
		this.mask = mask;
		string = new StringBuilder();
		maskTest = new ArrayList<Mask>(mask.length());
		nullablePositions = new ArrayList<Integer>();
		if(replaceValue) {
			configureUserView();
		} else {
			configureMask();
		}
		getNextPosition(0);
	}
	
	public void setPlaceHolder(char placeHolder) {
		this.placeholder = placeHolder;
	}

	private void configureMask() {
		for (int index = 0; index < mask.length(); index++) {
			char character = mask.charAt(index);
			createCorrectMaskAndPlaceholder(character, index);
		}
		proccessedMask = string.toString();
	}
	
	private void configureUserView() {
		configureMask();
		super.setText(proccessedMask);
		valueChange(false);
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

	private void addMaskStrategyAndCharacterPlaceHolder(Mask maskStrategy, char characterPlaceholder) {
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
		return getText().length() + 1;
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
		Mask maskStrategy = maskTest.get(getAvaliableCursorPos(getCursorPos()));
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
	
	@Override 
	public void onBrowserEvent(Event event) { 
	    if(event.getTypeInt() == Event.ONPASTE) {
	    	super.setText("");
	    	processOriginalPasteEvent(event);
	    	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					formatPaste();
				}
			});
	    	
	    } else {
	    	super.onBrowserEvent(event);
	    }
	}
	
	protected void processOriginalPasteEvent(Event evt) {
		super.onBrowserEvent(evt);
	}
	
	protected void formatPaste() {
		setText(formatString(super.getText()));
	}
	
	protected String formatString(final String value) {
		if(value == null || value.trim().isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder(proccessedMask);
		int valueIndex = 0;
		char[] valueChars = value.toCharArray();
		for(int i=0; i<maskTest.size(); i++) {
			Mask maskStrategy = maskTest.get(i);
			if(maskStrategy != null) {
				if(valueIndex<valueChars.length) {
					while(valueIndex<valueChars.length) {
						char s = valueChars[valueIndex++];
						if(nullablePositions.contains(i) || maskStrategy.isValid(s)) {
							if(s == ' ') {
								sb.setCharAt(i, ' ');
							} else {
								sb.setCharAt(i, maskStrategy.getChar(s));
							}
							break;
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	private void showUserInput(char character) {
		if(getText().isEmpty()) {
			configureUserView();
		}
		if(getText().length() < maskTest.size()) {
			
		}
		int currentPosition = getAvaliableCursorPos(getCursorPos());
		string.setCharAt(currentPosition, character); 
		super.setText(string.toString());
		setCursorPos(getNextPosition(currentPosition));
		valueChange(false);
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
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_HOME && !event.isShiftKeyDown()) {
			setCursorPositionAndPreventDefault(event, getPreviousPosition(0));
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_END && !event.isShiftKeyDown()) {
			setCursorPositionAndPreventDefault(event, getLastPosition());
		} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if(isFieldIfIncomplete()) {
				cleanText();
			}
			super.onKeyDown(event);
		} else {
			super.onKeyDown(event);
		}
	}
	
	private void deleteTextOnKeyDown(KeyDownEvent event) {
		if(!getSelectedText().isEmpty()) {
			String selected = getSelectedText();
			for(int i=(selected.length()-1); i>=0; i--) {
				int index = getText().indexOf(Character.toString(selected.charAt(i)));
				deleteCharacter(index);
			}
			setCursorPos(0);
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
			super.setText(string.toString());
			valueChange(false);
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		if (getText().isEmpty()) {
			setMask(mask);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					setCursorPos(getAvaliableCursorPos(0));
				}
			});
		}
	}
	
	public int getAvaliableCursorPos(int desiredPosition) {
		int i = desiredPosition;
		for(;i<maskTest.size(); i++) {
			if(maskTest.get(i) != null) {
				break;
			}
		}
		return i;
	}

	public int getNextAvaliableCursorPos(int desiredPosition) {
		int i = desiredPosition;
		for(;i<maskTest.size(); i++) {
			if(maskTest.get(i) != null && string.charAt(i) == placeholder) {
				break;
			}
		}
		return i;
	}
	
	@Override
	protected boolean updateCursorPosition() {
		if(!isImmediate()) {
			return super.updateCursorPosition();
		}
		return false;
	}

	@Override
	public void setImmediate(boolean immediate) {
		super.setImmediate(immediate);
		this.immediate = true;
	}
	
	private boolean isImmediate() {
		return immediate;
	}
	
	@Override
	public void valueChange(boolean blurred) {
		if(!isFieldIfIncomplete()) {
			super.valueChange(blurred);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if(isFieldIfIncomplete()) {
			cleanText();
		} else {
			super.onBlur(event);
		}
	}
	
	private void cleanText() {
		super.setText("");
		super.valueChange(true);
	}
	
	protected boolean isFieldIfIncomplete() {
		if(string == null || maskTest == null) {
			return true;
		}
		for (int index = 0; index < string.length(); index++) {
			char character = string.charAt(index);
			if (maskTest.get(index) != null && character == placeholder) {
				return true;
			}
		}
		return false;
	}
	
	protected String unmask() {
		return unmask(super.getText());
	}
	
	protected String unmask(final String value) {
		char[] masks = Constants.MASK_REPRESENTATIONS;
		Arrays.sort(masks);
		if(value == null || value.trim().isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder(value);
		String m = mask.replaceAll("\\+", "");
		int removedChars = 0;
		for(int i = 0; i<m.length(); i++) {
			char s = m.charAt(i);
			if(Arrays.binarySearch(masks, s) < 0) {
				if(i < value.length() && sb.charAt(i-removedChars) == s) {
					sb.deleteCharAt(i-removedChars);
					removedChars++;
				}
			}
		}
		return sb.toString().replaceAll(String.valueOf(placeholder), "");
	}
	
}