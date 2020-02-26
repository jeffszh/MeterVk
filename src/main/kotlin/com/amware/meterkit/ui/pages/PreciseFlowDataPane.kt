package com.amware.meterkit.ui.pages

import com.amware.meterkit.ui.MainLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

@Route(value = "precise-flow-data", layout = MainLayout::class)
class PreciseFlowDataPane : VerticalLayout() {

	init {
		defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
		add(H1("高精度流量數據"))
		add(Button("讀取"))
		width = "1024px"
	}

}
