package com.andersonfreitas.vaadin.maskedtextfield;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MaskedTextField;
import com.vaadin.ui.NumericField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class DemoApplication extends Application {
	@Override
	public void init() {
		final Window mainWindow = new Window("Vaadin MaskedTextField Demo Application");
		
		TabSheet tabSheet = new TabSheet();
		
		FormLayout numbers = new FormLayout();
		
		final MaskedTextField phoneField = new MaskedTextField("Phone (##) ####-####", "+55 (##) ####-####");
		
		Button btnShowValue = new Button("Show value");
		btnShowValue.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				mainWindow.showNotification((String) phoneField.getValue());
			}
		});
		
		numbers.addComponent(phoneField);
		numbers.addComponent(btnShowValue);
		
		numbers.addComponent(new MaskedTextField("UU###-###-###LL", "UU###-###-###LL"));
		numbers.addComponent(new MaskedTextField("USPS Express Mail", "EU#########'US"));
		numbers.addComponent(new MaskedTextField("Alphanumeric", "AAAAAAAAAA"));
		numbers.addComponent(new MaskedTextField("Hex 0xHHHHHHHH", "0xHHHHHHHH"));
		numbers.addComponent(new MaskedTextField("Signed number", "~####"));
		numbers.addComponent(new MaskedTextField("Escape #2010", "'#####"));
		numbers.addComponent(new MaskedTextField("'2010'", "''####''"));
		
		FormLayout misc = new FormLayout();
		misc.addComponent(new MaskedTextField("Capitalized name", "ULLLLLLLLLLLLLL"));
		misc.addComponent(new MaskedTextField("Upper-case letters only", "UUUUUUUUU"));
		misc.addComponent(new MaskedTextField("Lower-case letters only", "LLLLLLLLL"));
		misc.addComponent(new MaskedTextField("Any letter", "????????"));
		misc.addComponent(new MaskedTextField("Anything", "***********"));
		misc.addComponent(new MaskedTextField("Time", "##:##"));
		misc.addComponent(new MaskedTextField("Some preffix", "TR-####"));
		
		FormLayout numeric = new FormLayout();
		numeric.addComponent(new Label("Use UP and DOWN keys to change the value"));
		numeric.addComponent(new NumericField("Numeric Value"));
		
		tabSheet.addTab(numbers, "Numbers", null);
		tabSheet.addTab(misc, "Misc", null);
		tabSheet.addTab(numeric, "NumericField", null);
		mainWindow.addComponent(tabSheet);
		
		setMainWindow(mainWindow);
	}
}
