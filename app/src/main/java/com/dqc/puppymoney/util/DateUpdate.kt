package com.dqc.puppymoney.util

import android.os.Handler
import android.os.Message
import com.dqc.puppymoney.interfaces.IDateCallback
import com.dqc.puppymoney.bean.DateBean
import java.util.*

class DateUpdate: Handler() {
    private var mDateCallback: IDateCallback ?= null
    private var mCalender: Calendar? = null
    private var dateBean: DateBean = DateBean(0, 0, 0, 0, 0,  0, 0)

    companion object {
        val HANDLER_WHAT: Int = 0
    }

    fun setDataCallBack(dateCallback: IDateCallback) {
        mDateCallback = dateCallback
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        getCurrentDate()
        sendEmptyMessageDelayed(HANDLER_WHAT, 1000)
    }

    fun getCurrentDate() {
        mCalender = Calendar.getInstance()

        yearsRefresh(dateBean)
        monthRefresh(dateBean)
        timeRefresh(dateBean)

    }

    private fun yearsRefresh(dateBean: DateBean) {

        if (dateBean.mCurrentYear != mCalender?.get(Calendar.YEAR)!!) {
            dateBean.mCurrentYear = mCalender?.get(Calendar.YEAR)!!
            if (mDateCallback != null)
                mDateCallback?.onYearsRefresh(dateBean)
        }
    }

    private fun monthRefresh(dateBean: DateBean) {

        var isMonthChanged = false
        var isDaysOfMonthChanged = false
        var isDaysOfWeekChanged = false

        if (dateBean.mCurrentMonth != mCalender?.get(Calendar.MONTH)!!) {
            isMonthChanged = true
            dateBean.mCurrentMonth = mCalender?.get(Calendar.MONTH)!!
        }

        if (dateBean.mCurrentDaysOfMonth != mCalender?.get(Calendar.DAY_OF_MONTH)!!) {
            isDaysOfMonthChanged = true
            dateBean.mCurrentDaysOfMonth = mCalender?.get(Calendar.DAY_OF_MONTH)!!
        }

        if (dateBean.mCurrentDaysOfWeek != mCalender?.get(Calendar.DAY_OF_WEEK)!!) {
            isDaysOfWeekChanged = true
            dateBean.mCurrentDaysOfWeek = mCalender?.get(Calendar.DAY_OF_WEEK)!!
        }

        if (isMonthChanged
                || isDaysOfMonthChanged
                || isDaysOfWeekChanged) {
            if (mDateCallback != null)
                mDateCallback?.onMonthRefresh(dateBean)
        }
    }

    private fun timeRefresh(dateBean: DateBean) {

        if (dateBean.mCurrentHours != mCalender?.get(Calendar.HOUR_OF_DAY)!!)
            dateBean.mCurrentHours = mCalender?.get(Calendar.HOUR_OF_DAY)!!

        if (dateBean.mCurrentMinute != mCalender?.get(Calendar.MINUTE)!!)
            dateBean.mCurrentMinute = mCalender?.get(Calendar.MINUTE)!!

        if (dateBean.mCurrentSecond != mCalender?.get(Calendar.SECOND)!!) {
            dateBean.mCurrentSecond = mCalender?.get(Calendar.SECOND)!!
            if (mDateCallback != null)
                mDateCallback?.onTimeReferesh(dateBean)
        }
    }

}