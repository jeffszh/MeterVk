package com.amware.meterkit.ui

import com.vaadin.flow.component.notification.Notification

fun showToast(text: String) {
	Notification.show(text, 3000, Notification.Position.MIDDLE)
}
