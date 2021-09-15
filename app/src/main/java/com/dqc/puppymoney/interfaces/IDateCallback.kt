package com.dqc.puppymoney.interfaces

import com.dqc.puppymoney.bean.DateBean

interface IDateCallback {
    fun onYearsRefresh(dateBean: DateBean)
    fun onMonthRefresh(dateBean: DateBean)
    fun onTimeReferesh(dateBean: DateBean)
}