package com.kushcabbage.friday_android.exceptions

import com.kushcabbage.friday_android.IModifyUI

class RequestsExceededException : Exception() {


    fun printToScreen(modifyUI: IModifyUI) {
        modifyUI.showException("Accuweather requests exceeded")

    }

    fun printStackTrace(modifyUI: IModifyUI) {
        super.printStackTrace()

    }
}
