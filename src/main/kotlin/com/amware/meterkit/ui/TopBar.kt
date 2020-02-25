package com.amware.meterkit.ui

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.RouterLayout

class TopBar : VerticalLayout(), RouterLayout {

	init {
		val tabMap = mapOf(
				Tab("901F\n累計流量") to "901F",
				Tab("902F\n高精度數據") to "902F"
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
