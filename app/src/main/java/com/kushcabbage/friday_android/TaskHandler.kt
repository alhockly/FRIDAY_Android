package com.kushcabbage.friday_android

import com.kushcabbage.friday_android.AsyncTasks.AccuweatherCurrentWeatherAsyncTask

class TaskHandler(UiModify : IModifyUI) : IApiMVC.DataControllerOps {

    var iModifyUI = UiModify

    override fun refreshCurrentTemp() {

        AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME].toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME].toString(), iModifyUI).execute()
    }
}