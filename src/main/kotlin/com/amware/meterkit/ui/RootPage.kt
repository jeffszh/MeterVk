package com.amware.meterkit.ui

import com.amware.meterkit.showToast
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route

@Route(value = "", layout = MainLayout::class)
class RootPage : VerticalLayout() ,BeforeEnterObserver{

	override fun beforeEnter(event: BeforeEnterEvent?) {
		event?.forwardTo("flow-data")
//		event?.rerouteTo("flow-data")
	}

	init {
		add(Button("歡迎！\n這是首頁。") {
			showToast("可以了。")
		})

//		thread {
//			Thread.sleep(1000)
//			UI.getCurrent().navigate("flow-data")
//		}.start()
	}

}
