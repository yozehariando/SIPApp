package com.ikea.aceh.unicef.sip

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.co.portofolio.portofolio.helper.launchActivity
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/trajan.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
        setContentView(R.layout.activity_splash)

        val pInfo: PackageInfo
        val request = VersionRequestJson()
        val tvVersion = findViewById(R.id.textView5) as TextView
        request.version = "1"
        request.application = "0"
        try
        {
            pInfo = packageManager.getPackageInfo(packageName, 0)
            request.version = (pInfo.versionCode).toString() + ""
            tvVersion.text = "Version: " + pInfo.versionName
        }
        catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        start()
    }

     override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun start(){
        Handler().postDelayed({
                launchActivity<MainActivity>()
                finish()
        },3000)
    }
}
