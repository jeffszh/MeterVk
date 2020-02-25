package com.amware.meterkit.ui.pages

import com.amware.meterkit.ui.MainLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

@Route(value = "FlowData", layout = MainLayout::class)
class FlowDataPane : VerticalLayout() {

	init {
//		defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
		add(H1("流量數據"))
		add(Button("讀取"))
		width = "1024px"
	}

}
