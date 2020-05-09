package com.kushcabbage.friday_android

interface IApiMVC {


    interface ViewOps{
        fun displayOnOff(isOn : Boolean)
    }


    interface DataControllerOps{
        fun refreshCurrentTemp()
    }
}