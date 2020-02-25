package com.amware.meterkit.ui

import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.TextField

class AddressPane : VerticalLayout() {

	init {
		defaultHorizontalComponentAlignment=FlexComponent.Alignment.CENTER
		add(RadioButtonGroup<String>().apply {
			setItems("使用廣播地址", "指定具體地址")
		})
		add(TextField("地址", "請輸入具體地址"))
//		width = "100%"
	}

}
