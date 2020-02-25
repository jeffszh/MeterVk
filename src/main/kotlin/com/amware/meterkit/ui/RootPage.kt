package com.amware.meterkit.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

@Route(value = "", layout = MainLayout::class)
class RootPage : VerticalLayout() {

	init {
		add(Button("歡迎！\n這是首頁。") {
			showToast("可以了。")
		})
	}

}
