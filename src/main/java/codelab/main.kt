import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Clase base de todos los dispositivos
open class SmartDevice(val name: String, val category: String) {

    // Solo se puede leer desde fuera; solo las clases hijas pueden cambiarla
    var deviceStatus = "online"
        protected set

    open val deviceType = "unknown"

    open fun turnOn() {
        deviceStatus = "on"
    }

    open fun turnOff() {
        deviceStatus = "off"
    }

    // DESAFÍO: imprime la información del dispositivo
    fun printDeviceInfo() {
        println("Device name: $name, category: $category, type: $deviceType")
    }
}

// Regula que un valor se mantenga dentro de un rango (propiedad delegada)
class RangeRegulator(
    initialValue: Int,
    private val minValue: Int,
    private val maxValue: Int
) : ReadWriteProperty<Any?, Int> {

    var fieldData = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return fieldData
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        if (value in minValue..maxValue) {
            fieldData = value
        }
    }
}

// Televisor inteligente
class SmartTvDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Smart TV"

    private var speakerVolume by RangeRegulator(initialValue = 2, minValue = 0, maxValue = 100)
    private var channelNumber by RangeRegulator(initialValue = 1, minValue = 0, maxValue = 200)

    fun increaseSpeakerVolume() {
        speakerVolume++
        println("Speaker volume increased to $speakerVolume.")
    }

    // DESAFÍO: baja el volumen
    fun decreaseVolume() {
        speakerVolume--
        println("Speaker volume decreased to $speakerVolume.")
    }

    fun nextChannel() {
        channelNumber++
        println("Channel number increased to $channelNumber.")
    }

    // DESAFÍO: va al canal anterior
    fun previousChannel() {
        channelNumber--
        println("Channel number decreased to $channelNumber.")
    }

    override fun turnOn() {
        super.turnOn()
        println(
            "$name is turned on. Speaker volume is set to $speakerVolume and " +
                    "channel number is set to $channelNumber."
        )
    }

    override fun turnOff() {
        super.turnOff()
        println("$name turned off")
    }
}

// Luz inteligente
class SmartLightDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Smart Light"

    private var brightnessLevel by RangeRegulator(initialValue = 0, minValue = 0, maxValue = 100)

    fun increaseBrightness() {
        brightnessLevel++
        println("Brightness increased to $brightnessLevel.")
    }

    // DESAFÍO: baja el brillo
    fun decreaseBrightness() {
        brightnessLevel--
        println("Brightness decreased to $brightnessLevel.")
    }

    override fun turnOn() {
        super.turnOn()
        brightnessLevel = 2
        println("$name turned on. The brightness level is $brightnessLevel.")
    }

    override fun turnOff() {
        super.turnOff()
        brightnessLevel = 0
        println("Smart Light turned off")
    }
}

// La casa: controla los dispositivos
class SmartHome(
    val smartTvDevice: SmartTvDevice,
    val smartLightDevice: SmartLightDevice
) {

    // Cuántos dispositivos están encendidos (solo se cambia desde esta clase)
    var deviceTurnOnCount = 0
        private set

    private fun tvIsOn(): Boolean {
        val encendido = smartTvDevice.deviceStatus == "on"
        if (!encendido) println("Smart TV is not on. Turn it on first.")
        return encendido
    }

    private fun lightIsOn(): Boolean {
        val encendido = smartLightDevice.deviceStatus == "on"
        if (!encendido) println("Smart Light is not on. Turn it on first.")
        return encendido
    }

    // ----- Televisor -----
    fun turnOnTv() {
        if (smartTvDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartTvDevice.turnOn()
        } else {
            println("Smart TV is already on.")
        }
    }

    fun turnOffTv() {
        if (smartTvDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartTvDevice.turnOff()
        }
    }

    fun increaseTvVolume() {
        if (tvIsOn()) smartTvDevice.increaseSpeakerVolume()
    }

    fun decreaseTvVolume() {
        if (tvIsOn()) smartTvDevice.decreaseVolume()
    }

    fun changeTvChannelToNext() {
        if (tvIsOn()) smartTvDevice.nextChannel()
    }

    fun changeTvChannelToPrevious() {
        if (tvIsOn()) smartTvDevice.previousChannel()
    }

    fun printSmartTvInfo() {
        if (tvIsOn()) smartTvDevice.printDeviceInfo()
    }

    // ----- Luz -----
    fun turnOnLight() {
        if (smartLightDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartLightDevice.turnOn()
        } else {
            println("Smart Light is already on.")
        }
    }

    fun turnOffLight() {
        if (smartLightDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartLightDevice.turnOff()
        }
    }

    fun increaseLightBrightness() {
        if (lightIsOn()) smartLightDevice.increaseBrightness()
    }

    fun decreaseLightBrightness() {
        if (lightIsOn()) smartLightDevice.decreaseBrightness()
    }

    fun printSmartLightInfo() {
        if (lightIsOn()) smartLightDevice.printDeviceInfo()
    }

    // ----- Todo -----
    fun turnOffAllDevices() {
        turnOffTv()
        turnOffLight()
    }
}

fun main() {
    val smartTvDevice = SmartTvDevice("Android TV", "Entertainment")
    val smartLightDevice = SmartLightDevice("Google Light", "Utility")
    val smartHome = SmartHome(smartTvDevice, smartLightDevice)

    println("--- Con los dispositivos apagados ---")
    smartHome.decreaseTvVolume()
    smartHome.changeTvChannelToPrevious()
    smartHome.decreaseLightBrightness()

    println("\n--- Encendiendo ---")
    smartHome.turnOnTv()
    smartHome.turnOnLight()
    println("Dispositivos encendidos: ${smartHome.deviceTurnOnCount}")

    println("\n--- Información ---")
    smartHome.printSmartTvInfo()
    smartHome.printSmartLightInfo()

    println("\n--- Televisor ---")
    smartHome.increaseTvVolume()
    smartHome.decreaseTvVolume()
    smartHome.changeTvChannelToNext()
    smartHome.changeTvChannelToPrevious()

    println("\n--- Luz ---")
    smartHome.increaseLightBrightness()
    smartHome.decreaseLightBrightness()

    println("\n--- Apagando todo ---")
    smartHome.turnOffAllDevices()
    println("Dispositivos encendidos: ${smartHome.deviceTurnOnCount}")

    println("\n--- Después de apagar ---")
    smartHome.decreaseTvVolume()
}