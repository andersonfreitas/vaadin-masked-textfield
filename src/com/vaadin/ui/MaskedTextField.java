package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VMaskedTextField;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.TextField;

@ClientWidget(VMaskedTextField.class)
public class MaskedTextField extends TextField {
	private static final long serialVersionUID = -5168618178262041249L;

	private String mask;

	public MaskedTextField() {
	}

	public MaskedTextField(String caption) {
		setCaption(caption);
	}

	public MaskedTextField(String caption, String mask) {
		setCaption(caption);
		setMask(mask);
	}

	public MaskedTextField(Property dataSource) {
		super(dataSource);
	}

	public MaskedTextField(String caption, Property dataSource) {
		super(caption, dataSource);
	}

	public String getMask() {
		return mask;
	}
	
	public void setMask(String mask) {
		this.mask = mask;
		requestRepaint();
	}
	
	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		if (mask != null) {
			target.addAttribute("mask", mask);
		}
	}
}
