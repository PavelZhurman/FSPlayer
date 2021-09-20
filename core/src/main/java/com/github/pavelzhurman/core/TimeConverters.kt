package com.github.pavelzhurman.core

object TimeConverters {

    fun convertDurationFromDoubleSecToIntMillis(doubleSec: Double): Int {
        return (doubleSec * 1000).toInt()
    }

    fun convertFromMillisToMinutesAndSeconds(millis: Long): String {
        return if (millis > 0L) {
            val hours: Long = millis / 3600000L
            val minutes: Long = (millis - (hours * 3600000L)) / 60000L
            val seconds: Long = (millis - (minutes * 60000L)) / 1000L
            if (hours <= 0L) {
                if (seconds < 10L) "$minutes:0$seconds"
                else "$minutes:$seconds"
            } else {
                if (minutes < 10) "$hours:0$minutes:$seconds"
                else "$hours:$minutes:$seconds"
            }
        } else "0:00"
    }

    fun convertFromMillisToPercents(currentPosition: Long, duration: Int): Int {
        return if (currentPosition != 0L) {
            if (duration != 0) {
                val result: Double =
                    (currentPosition.toDouble() / duration.toDouble()) * 100.0
                result.toInt()
            } else 0
        } else 0
    }

}