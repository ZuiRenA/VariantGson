package com.variant_gson.core.internal

import java.lang.NumberFormatException

/**
 * Come from Gson
 *
 *
 * Utility to check the major Java version of the current JVM.
 */
object JavaVersion {

    private val majorJavaVersion: Int get() {
        val javaVersion = System.getProperty("java.version")
        return getMajorJavaVersion(javaVersion)
    }


    // Visible for testing only
    private fun getMajorJavaVersion(javaVersion: String): Int {
        var version = parseDotted(javaVersion)
        if (version == -1) {
            version = extractBeginningInt(javaVersion)
        }
        
        return if (version == -1) 6 else version
    }

    // Parses both legacy 1.8 style and newer 9.0.4 style
    private fun parseDotted(javaVersion: String): Int = try {
        val parts = javaVersion.split("[._]".toRegex())
        val firstVer = parts[0].toInt()
        if (firstVer == 1 && parts.size > 1) {
            parts[1].toInt()
        } else {
            firstVer
        }
    } catch (e: NumberFormatException) {
        -1
    }

    private fun extractBeginningInt(javaVersion: String): Int = try {
        val num = StringBuilder()
        for (c in javaVersion) {
            if (Character.isDigit(c)) {
                num.append(c)
            } else {
                break
            }
        }

        num.toString().toInt()
    } catch (e: NumberFormatException) {
        -1
    }
}