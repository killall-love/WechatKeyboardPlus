package io.github.uhsk.keyboard.xposed

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.uhsk.keyboard.commons.XC_MethodHookSafe
import io.github.uhsk.keyboard.constants.ConstantVectorDrawable
import io.github.uhsk.keyboard.utils.Slf4jUtil
import io.github.uhsk.keyboard.utils.VectorDrawableCreator
import org.joor.Reflect
import org.slf4j.Logger

class ClipboardXposed : IXposedHookLoadPackage {

    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        if (loadPackageParam.packageName != "com.tencent.wetype") {
            return
        }
        XposedHelpers.findAndHookMethod(Application::class.java, "onCreate", mXCMethodApplicationOnCreate)
    }


    private val mXCMethodApplicationOnCreate: XC_MethodHook = object : XC_MethodHookSafe() {
        override val mLogger: Logger
            get() = org.slf4j.LoggerFactory.getLogger(this.javaClass)

        override fun beforeHookedMethodSafe(methodHookParam: MethodHookParam) {
            super.beforeHookedMethodSafe(methodHookParam)
            val application: Application = methodHookParam.thisObject as Application
            Slf4jUtil.init(context = application)
            XposedBridge.log("mXCMethodApplicationOnCreate")
            XposedHelpers.findAndHookMethod("com.tencent.wetype.plugin.hld.view.settingkeyboard.S10SettingPage0PlusView", application.classLoader, "C", mXCMethodS10SettingPage0PlusViewC)
            XposedHelpers.findAndHookMethod("com.tencent.wetype.plugin.hld.view.settingkeyboard.S10SettingPage0PlusView", application.classLoader, "z", mXCMethodS10SettingPage0PlusViewZ)
        }
    }

    private val mXCMethodS10SettingPage0PlusViewC: XC_MethodHook = object : XC_MethodHookSafe() {

        override val mLogger: Logger
            get() = org.slf4j.LoggerFactory.getLogger(this.javaClass)

        override fun afterHookedMethodSafe(methodHookParam: MethodHookParam) {
            mLogger.info("LOG:ClipboardXposed:safeAfterHookedMethod methodHookParam={}", methodHookParam.thisObject)
            val settingItemViewLists: ArrayList<View> = Reflect.on(methodHookParam.thisObject).field("L").get()
            for (itemView in settingItemViewLists) {
                val titleTextView: TextView = itemView.findViewById(itemView.context.resources.getIdentifier("tm", "id", itemView.context.packageName))
                mLogger.info("LOG:ClipboardXposed:safeAfterHookedMethod text={} view={}", titleTextView.text, itemView)
            }

            val thisObject: View = methodHookParam.thisObject as View
            val context: Context = thisObject.context

            val clipboardItemTag: Any = Reflect.onClass("com.tencent.wetype.plugin.hld.view.settingkeyboard.a", context.classLoader).create(
                Reflect.onClass("com.tencent.wetype.plugin.hld.l", context.classLoader).field("s10_plus_emoji").get(),
                Reflect.onClass("com.tencent.wetype.plugin.hld.p", context.classLoader).field("icon_setting_emoji").get(),
                Reflect.onClass("com.tencent.wetype.plugin.hld.q", context.classLoader).field("ime_s10_emoji").get(),
                false,
                false,
                24,
                null
            ).get()
            val clipboardItemView = LayoutInflater.from(context).inflate(
                Reflect.onClass("com.tencent.wetype.plugin.hld.n", context.classLoader).field("ime_keyboard_s10_page1_keyboard_item_view").get<Int>(),
                Reflect.on(thisObject).call("getBinding").field("c").get(),
                false
            )
            clipboardItemView.id = Reflect.on(clipboardItemTag).call("b").get()
            clipboardItemView.setTag(clipboardItemTag)
            clipboardItemView.setOnClickListener(mOnClickListener)
            settingItemViewLists.add(clipboardItemView)
        }
    }

    private val mXCMethodS10SettingPage0PlusViewZ: XC_MethodHook = object : XC_MethodHookSafe() {
        override val mLogger: Logger
            get() = org.slf4j.LoggerFactory.getLogger(this.javaClass)

        override fun afterHookedMethodSafe(methodHookParam: MethodHookParam) {
            super.afterHookedMethodSafe(methodHookParam)
            val settingItemViewLists: ArrayList<View> = Reflect.on(methodHookParam.thisObject).field("L").get()

            val clipboardS10SettingItemView = settingItemViewLists.last()
            val vectorDrawable: Drawable = VectorDrawableCreator.getVectorDrawable(
                clipboardS10SettingItemView.context,
                18,
                18,
                24f,
                24f,
                ConstantVectorDrawable.s10_plus_custom_clipboard
            )

            Reflect.on(clipboardS10SettingItemView).call("setText", "剪贴板")
            Reflect.on(clipboardS10SettingItemView).call("getIconIv").call("setImageDrawable", vectorDrawable)
        }
    }

    private val mOnClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(it.context, "粘贴板功能制作中", Toast.LENGTH_LONG).show()
    }

}
