package com.amware.meterkit.ui

import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.RouterLayout

class TopBar : VerticalLayout(), RouterLayout {

	init {
		defaultHorizontalComponentAlignment=FlexComponent.Alignment.CENTER
		val tabMap = mapOf(
				Tab("流量數據") to "FlowData",
				Tab("高精度流量數據") to "PreciseFlowData"
		)
		val menuBar = Tabs()
		tabMap.forEach {
			menuBar.add(it.key)
		}
		menuBar.addSelectedChangeListener {
			val target = tabMap[menuBar.selectedTab]
			ui.get().navigate(target)
		}
		add(menuBar)
	}

}
