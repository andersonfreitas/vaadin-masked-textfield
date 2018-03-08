package org.vaadin.addons.maskedtextfield.client;

import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Event;

public class PrefixedMaskedTextFieldWidget extends MaskedTextFieldWidget {

	private String defaultMask;
	private Map<String, String> masks;
	
	public PrefixedMaskedTextFieldWidget() {
		super();
	}

	public void setMasks(Map<String, String> masksByPrefix) {
		this.masks = masksByPrefix;
	}

	public void setDefaultMask(String mask) {
		defaultMask = mask;
	}
	
	@Override
	public void setMask(String mask) {
		if(getText() == null || getText().isEmpty() || this.mask == null) {
			super.setMask(mask);
		}
	}
	
	/*
	private boolean isMaskCompatibleWithValue(String mask) {
		if(getText() == null || getText().isEmpty()) {
			
		}
		return true;
	}
	*/

	@Override
	public void onKeyPress(KeyPressEvent event) {
		super.onKeyPress(event);
		if(!masks.isEmpty()) {
			String v = unmask();
			if(v != null && !v.isEmpty()) {
				checkForPrefix(v);
			}
		}
	}
	
	protected void checkForPrefix(String v) {
		String maskForPrefix = getMaskForPrefix(v);
		if(updateMaskForPrefix(maskForPrefix)) {
			updateValueOnNewMask(v);
		}
	}
	
	private String getMaskForPrefix(String value) {
		value = formatString(unmask(value));
		if(masks != null) {
			for(String prefix : masks.keySet()) {
				if(value.startsWith(prefix)) {
					return masks.get(prefix);
				}
			}
		}
		return null;
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		if(event.getTypeInt() == Event.ONPASTE) {
			super.setText("");
			processOriginalPasteEvent(event);
			if(!masks.isEmpty()) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						String v = unmask();
						if(v != null && !v.isEmpty()) {
							String maskForPrefix = getMaskForPrefix(v);
							updateMaskForPrefix(maskForPrefix);
						}
						formatPaste();
					}
				});
				
			}
		} else {
			processOriginalPasteEvent(event);
		}
	}
	
	@Override
	public void setText(String text) {
		if(text != null && !text.isEmpty()) {
			updateMaskForPrefix(getMaskForPrefix(formatString(unmask(text))));
		}
		super.setText(unmask(text));
	}
	
	private boolean updateMaskForPrefix(String maskForPrefix) {
		if(mask != null) {
			if(maskForPrefix != null) {
				if(!mask.equals(maskForPrefix)) {
					super.setMask(maskForPrefix, false);
					return true;
				}
			} else {
				if(!mask.equals(defaultMask)) {
					super.setMask(defaultMask, false);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Updates the new value on field, without check if it is complete.
	 * @param v
	 */
	protected void updateValueOnNewMask(String v) {
		String formated = formatString(v); 
		setText(formated, false);
		setCursorPos(getNextAvaliableCursorPos(0));
	}
	
}
