package com.amware.meterkit.ui

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.ParentLayout
import com.vaadin.flow.router.RouterLayout

@ParentLayout(SerialPortPane::class)
class MainLayout : VerticalLayout(), RouterLayout {

	init {
		add(AddressPane())
	}

}
