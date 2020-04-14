package com.kushcabbage.friday_android.exceptions;

import com.kushcabbage.friday_android.IModifyUI;

public class RequestsExceededException extends Exception {


    public void printToScreen(IModifyUI modifyUI) {
        modifyUI.showException("Accuweather requests exceeded");

    }
}
