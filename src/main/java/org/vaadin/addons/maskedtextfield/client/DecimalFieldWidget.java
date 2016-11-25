package org.vaadin.addons.maskedtextfield.client;

import java.util.Arrays;

import org.vaadin.addons.maskedtextfield.shared.Constants;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VTextField;

/**
 * Widget para controlar a edicao de valores de moeda 
 * @author Eduardo Frazao
 *
 */
public class DecimalFieldWidget extends VTextField implements KeyPressHandler, BlurHandler, FocusHandler {
	
	
	private char decimalSeparator;
	private char groupingSeparator;
	private String mask;
	
	private Number maxValue = Double.MAX_VALUE;
	
	private NumberFormat formatter;
	private NumberFormat defaultFormatter = NumberFormat.getDecimalFormat();
	
	private boolean selectTextOnFocus = false;
	
	protected static char[] acceptedCharSet = {
		(char) KeyCodes.KEY_BACKSPACE,
		(char) KeyCodes.KEY_TAB,
		(char) KeyCodes.KEY_DELETE,  
		(char) KeyCodes.KEY_END,
		(char) KeyCodes.KEY_ENTER,
		(char) KeyCodes.KEY_UP,
		(char) KeyCodes.KEY_ESCAPE,
		(char) KeyCodes.KEY_HOME,
		(char) KeyCodes.KEY_LEFT,
		(char) KeyCodes.KEY_PAGEDOWN,
		(char) KeyCodes.KEY_PAGEUP,
		(char) KeyCodes.KEY_RIGHT
	};
	
	static {
		Arrays.sort(acceptedCharSet);
	}
	
	public boolean isSelectTextOnFocus() {
		return selectTextOnFocus;
	}

	public void setSelectTextOnFocus(boolean selectTextOnFocus) {
		this.selectTextOnFocus = selectTextOnFocus;
	}

	public DecimalFieldWidget() {
		setAlignment(TextAlignment.RIGHT);
		
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addFocusHandler(this);
		sinkEvents(Event.ONPASTE);
		
		NumberFormat.setForcedLatinDigits(false);
		formatter = NumberFormat.getFormat("#,###.00");
	}
	
	private boolean isCopyOrPasteEvent(KeyPressEvent evt) {
		if(evt.isControlKeyDown()) {
			return Character.toString(evt.getCharCode()).toLowerCase().equals("c") ||
					Character.toString(evt.getCharCode()).toLowerCase().equals("v");
		}
		return false;
	}
	
	private boolean isAcceptedKey(char charCode) {
		if(charCode == groupingSeparator) {
			return false;
		}
		if(charCode == decimalSeparator) {
			if(!mask.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
				return false;
			}
			String str = getText().trim();
			if(!str.isEmpty()) {
				return !str.contains(Character.toString(decimalSeparator));
			} else {
				return false;
			}
		}
		return Character.isDigit(charCode)
				|| charCode == decimalSeparator 
				|| Arrays.binarySearch(acceptedCharSet, charCode) >= 0;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		if(!isCopyOrPasteEvent(event)) {
			if (event.getCharCode() != Constants.EMPTY_CHAR && !isAcceptedKey(event.getCharCode()) ) {
				cancelKey();
			} else if(!getText().trim().isEmpty() && formatter.parse(getText()) >= maxValue.doubleValue()) {
				cancelKey();
			}
		}
	}
	
	
	@Override
	public void onKeyDown(KeyDownEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			
			refreshValue();
			
		}
		super.onKeyDown(event);
	}

	@Override
	public void onBlur(BlurEvent event) {
		refreshValue();
		super.onBlur(event);
	}
	
	@Override
	public void onFocus(FocusEvent event) {
		super.onFocus(event);
		if(selectTextOnFocus) {
			String text = getText();
			if(text != null && !text.isEmpty()) {
				setSelectionRange(0, text.length());
			}
		}
	}

	@Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            onPaste(event);
        }
    }
	
	public void onPaste(Event event) {
		refreshValue();
		valueChange(false);
	}

	private void refreshValue() {
		updateText(reformatContent(null));
	}
	
	private void refreshValue(String withFormatedValue) {
		updateText(reformatContent(withFormatedValue));
	}
	
	private void updateText(String text) {
		String old = getText();
		String newValue = text;
		if(text != null || newValue != null) {
			if((old == null || !newValue.equals(old)) || newValue == null) {
				super.setText(text);
				valueChange(false);
			}
		}
		
	}
	
	protected String reformatContent(String value) {
	    String str = value == null ? getText() : value;
	    if(!str.trim().isEmpty()) {
	    	double amount = readDoubleFromFormattedValue(value);
	  	    return replaceSeparators(formatter.format(amount));
	    } 
	    return str;
	  }
	
	protected double parseDouble(String doubleNotationString) {
		try {
			return defaultFormatter.parse(doubleNotationString);
		} catch (NumberFormatException ex) {
			return 0.0;
		}
	}
	
	protected double readDoubleFromFormattedValue(String value) {
		String str = (value == null || value.trim().isEmpty()) ? getText().trim() : value;
		if(groupingSeparator != Constants.EMPTY_CHAR) {
			str = str.replaceAll(groupingSeparator == '.' ? "\\." : Character.toString(groupingSeparator), "");
		}
		if(decimalSeparator != Constants.EMPTY_CHAR) {
			if(decimalSeparator != '.') {
				str = str.replaceAll(Character.toString(decimalSeparator), ".");
			}
		}
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException ex) {
			return 0.0;
		}
	}
	
	protected String replaceSeparators(final String defaultFormatedValue) {
		String str = new String(defaultFormatedValue);
		if(groupingSeparator != Constants.EMPTY_CHAR) {
			str = str.replaceAll(Character.toString(Constants.FIXED_LOCALE_GROUPING_SEPARATOR), Character.toString(groupingSeparator));
		}
		if(decimalSeparator != Constants.EMPTY_CHAR) {
			if(mask.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
				int decimalLenght = mask.length() - mask.indexOf(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR) - 1;
				StringBuilder sb = new StringBuilder(str);
				sb.setCharAt(str.length() - decimalLenght - 1 , decimalSeparator);
				str = sb.toString();
			}
		}
		return str;
	}

	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(char decimalSeparator) {
		if(decimalSeparator != this.decimalSeparator) {
			this.decimalSeparator = decimalSeparator;
			refreshValue();
			valueChange(false);
		}
	}

	public char getGroupingSeparator() {
		return groupingSeparator;
	}

	public void setGroupingSeparator(char groupingSeparator) {
		if(groupingSeparator != this.groupingSeparator) {
			this.groupingSeparator = groupingSeparator;
			refreshValue();
			valueChange(false);
		}
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		if(mask != this.mask) {
			this.mask = mask;
			formatter = NumberFormat.getFormat(this.mask);
			refreshValue();
			valueChange(false);
		}
	}
	
	@Override
	public void setText(String value) {
		if(value == null) {
			super.setText(null);
			setValue(value);
		} else {
			refreshValue(value);
		}
	}
	
	@Override
	public void setValue(String value, boolean fireEvents) {
	    String oldValue = fireEvents ? getValue() : null;
	    setText(value);
	    if (fireEvents) {
	      String newValue = getValue();
	      ValueChangeEvent.fireIfNotEqual(this, oldValue, newValue);
	    }
	  }
	
}
