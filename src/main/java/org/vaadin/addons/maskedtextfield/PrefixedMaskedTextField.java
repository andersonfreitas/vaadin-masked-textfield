package org.vaadin.addons.maskedtextfield;

import java.util.Collections;
import java.util.Map;

import org.vaadin.addons.maskedtextfield.client.PrefixedMaskedTextFieldState;

public class PrefixedMaskedTextField extends MaskedTextField {

	private static final long serialVersionUID = 1L;

	public PrefixedMaskedTextField(String caption) {
		super(caption);
	}
	
	public PrefixedMaskedTextField() {
		super();
	}
	
	public PrefixedMaskedTextField(String caption, String defaultMask, Map<String, String> masksByPrefix) {
		super(caption, defaultMask);
		setMasks(masksByPrefix);
		getState().defaultMask = defaultMask;
	}
	
	public Map<String, String> getMasksByPrefix() {
		return Collections.unmodifiableMap(getState().masksByPrefix);
	}
	
	public String getMaskByPrefix(String prefix) {
		return getState().masksByPrefix.get(prefix);
	}
	
	public void addMask(String prefix, String mask) {
		getState().masksByPrefix.put(prefix, mask);
	}
	
	public void removeMask(String prefix) {
		getState().masksByPrefix.remove(prefix);
	}
	
	public void setMasks(Map<String, String> masksByPrefix) {
		getState().masksByPrefix.clear();
		getState().masksByPrefix.putAll(masksByPrefix);
	}
	
	@Override
	public void setMask(String mask) {
		super.setMask(mask);
		getState().defaultMask = mask;
	}

	@Override
	protected String unmask(final String value) {
		return super.unmask(value, getMaskForValue(value));
	}
	
	private String getMaskForValue(String value) {
		if(value != null && !value.isEmpty() && !getState().masksByPrefix.isEmpty()) {
			for(String prefix : getState().masksByPrefix.keySet()) {
				if(value.startsWith(prefix)) {
					return getState().masksByPrefix.get(prefix);
				}
			}
		}
		return super.getMask();
	}
	
	@Override
	public PrefixedMaskedTextFieldState getState() {
		return (PrefixedMaskedTextFieldState) super.getState();
	}
	
}
