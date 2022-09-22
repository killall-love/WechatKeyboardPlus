package io.github.uhsk.keyboard.commons

import de.robv.android.xposed.XC_MethodHook

abstract class XC_MethodHookSafe: XC_MethodHook() {

    abstract val mLogger: org.slf4j.Logger

    final override fun beforeHookedMethod(methodHookParam: MethodHookParam) {
        super.beforeHookedMethod(methodHookParam)
        try {
            beforeHookedMethodSafe(methodHookParam)
        } catch (e: Exception) {
            mLogger.error("LOG:XCSafeMethod:beforeHookedMethod e", e)
        }
    }

    final override fun afterHookedMethod(methodHookParam: MethodHookParam) {
        super.afterHookedMethod(methodHookParam)
        try {
            afterHookedMethodSafe(methodHookParam)
        } catch (e: Exception) {
            mLogger.error("LOG:XCSafeMethod:afterHookedMethod", e)
        }
    }

    open fun beforeHookedMethodSafe(methodHookParam: MethodHookParam) {

    }

    open fun afterHookedMethodSafe(methodHookParam: MethodHookParam) {
    }

}
