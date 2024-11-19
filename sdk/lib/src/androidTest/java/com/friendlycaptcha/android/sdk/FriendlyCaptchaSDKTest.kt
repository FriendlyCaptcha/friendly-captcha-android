package com.friendlycaptcha.android.sdk

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.friendlycaptcha.android.sdk.test.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class FriendlyCaptchaSDKInstrumentedTest {
//
//    @Rule
//    @JvmField
//    var rule: ActivityScenarioRule<TestActivity> = ActivityScenarioRule(TestActivity::class.java)

    @Test
    fun testInitialization() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val apiEndpoint = "global"

        val sdk = FriendlyCaptchaSDK(context = appContext, apiEndpoint = apiEndpoint)

        assertNotNull(sdk)
        // Additional assertions can be added here to verify the state of the SDK instance
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidApiEndpoint() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val invalidApiEndpoint = "invalid_endpoint"

        FriendlyCaptchaSDK(context = appContext, apiEndpoint = invalidApiEndpoint)
    }

    // This test fails because of a config error for the Activity (Unable to resolve activity for: Intent)..
    // If any Android expert sees this and can help fix it, that would be appreciated.
//    @Test
//    fun testWidget() {
//        val scenario: ActivityScenario<TestActivity> = rule.getScenario()
//
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        val sitekey = "some_invalid_sitekey"
//
//            scenario.onActivity { activity ->
//                val sdk = FriendlyCaptchaSDK(context = activity)
//                val widget = sdk.createWidget(sitekey)
//
//                assertNotNull(widget)
//                assertEquals(".UNINITIALIZED", widget.response)
//                assertEquals("uninitialized", widget.state)
//
//                // Attach the WebView to the activity's layout
//                val layout = activity.findViewById<ConstraintLayout>(R.id.test_activity_layout)
//                layout.addView(widget.webView)
//
//                val latch = CountDownLatch(1)
//
//                widget.setOnStateChangeListener { event ->
//                    println(event)
//                }
//
//                widget.setOnErrorListener { event ->
//                    // Error should happen - the sitekey is invalid.
//                    latch.countDown()
//                }
//
//                widget.setOnCompleteListener { event ->
//                    fail("Unexpected complete event: $event")
//                    latch.countDown()
//                }
//
//                widget.start()
//
//                if (!latch.await(5, TimeUnit.SECONDS)) {
//                    fail("Callback was not invoked within the timeout period")
//                }
//            }
//    }
}