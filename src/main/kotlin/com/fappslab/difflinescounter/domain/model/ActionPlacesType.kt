package com.fappslab.difflinescounter.domain.model

import com.intellij.openapi.actionSystem.ActionPlaces

enum class ActionPlacesType(val place: String) {
    STATUS_BAR_PLACE(place = ActionPlaces.STATUS_BAR_PLACE),
    KEYBOARD_SHORTCUT(place = ActionPlaces.KEYBOARD_SHORTCUT),
    UNKNOWN(place = ActionPlaces.UNKNOWN),
}
