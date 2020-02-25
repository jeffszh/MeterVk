package com.amware.meterkit.ui

import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.ParentLayout
import com.vaadin.flow.router.RouterLayout

@ParentLayout(TopBar::class)
class SerialPortPane : HorizontalLayout(), RouterLayout {

	init {
//		width = "100%"
		add(VerticalLayout().apply {
			add("串口參數")
			add(TextField("串口名", "COM1、COM2 或 ttyS0 之類"))
			add(TextField("波特率", "請輸入波特率"))
			add(TextField("數據位", "6-8"))
			add(TextField("停止位", "1 or 2"))
			add(TextField("奇偶校驗", "O、E 或 N"))
//			width = "50%"
		})
	}

}
