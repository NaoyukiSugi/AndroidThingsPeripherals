package com.pg_naoyuki.androidthings_peripherals

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class HomeActivity : Activity() {

    companion object {
        private const val TAG = "HomeActivity"
        private const val BUTTON_PIN_NAME = "GPIO6_IO14"
        private const val LED_PIN_NAME = "GPIO2_IO02"

    }

    // Driver for the GPIO button
    private lateinit var mButtonInputDriver: ButtonInputDriver

    // GPIO connection to LED output
    private lateinit var mLedGpio: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val pioManager = PeripheralManager.getInstance()
        Log.d(TAG, "Available GPIO: " + pioManager.gpioList)

        mButtonInputDriver = ButtonInputDriver(
                BUTTON_PIN_NAME,
                Button.LogicState.PRESSED_WHEN_LOW,
                KeyEvent.KEYCODE_SPACE)

        // Register with the framework
        mButtonInputDriver.register()

        mLedGpio = pioManager.openGpio(LED_PIN_NAME)
        // Configure as an output.
        mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the driver and close
        mButtonInputDriver.unregister()
        mButtonInputDriver.close()

        // Close the LED.
        mLedGpio.close()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn on the LED
            setLedValue(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn off the LED
            setLedValue(false)
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun setLedValue(value: Boolean) {
        mLedGpio.value = value
    }


}
