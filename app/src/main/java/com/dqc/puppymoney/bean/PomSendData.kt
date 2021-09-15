package com.dqc.puppymoney.bean

import java.io.Serializable

class PomSendData: Serializable {
    companion object {
        val POM_START_STATUS = 0
        val POM_PAUSE_STATUS = 1
        val POM_CANCEL_STATUS = 2
        val POM_OVER_STATUS = 3
        val POM_WORK_MODE = 4
        val POM_REST_MODE = 5
    }

    var mCurCountDownStatus: Int = -1
    var mCurMode: Int = -1
    var mMaxMinute: Int = 0
    var mCurMills: Int = 0
}
