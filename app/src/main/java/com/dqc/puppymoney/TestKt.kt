package com.dqc.puppymoney

fun main() {

//    var mMinute: Int = 8
//    var mPomodoroRadius: Float = 0f
//    var mPomodoroMinuteRadius: Float = 24f
//    var mPomodoroSecondRadius: Float = 9f
//    var mMinuteWithSecondOffset: Float = 6f
//    var mAngle: Float = 0f
//
//    mAngle = 360f / mMinute
//    var allOffset = mPomodoroMinuteRadius + mPomodoroSecondRadius + mMinuteWithSecondOffset
//
//    mPomodoroRadius = allOffset / Math.asin((mAngle / 2f) / 180 * Math.PI).toFloat()
//    print(mPomodoroRadius)

    var years = 10
    var inMoney = 15f
    var range = 0.24f
    var sum = 0f
    for (i in 0..years - 1) {
        sum += inMoney
        sum = sum * (1 + range)
        println(" ${i + 1}. " + sum)
    }
    val fomaccoNum = getFomaccoNum(5)
    println("fomaccoNum = $fomaccoNum")
}

private fun getFomaccoNum(n: Int): Int {
    if (n == 0 || n == 1) {
        return 1;
    } else {
        return getFomaccoNum(n - 1) + getFomaccoNum(n - 2)
    }
}