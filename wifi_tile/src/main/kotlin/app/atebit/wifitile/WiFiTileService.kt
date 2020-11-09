package app.atebit.wifitile

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Panel.ACTION_WIFI
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import java.math.BigInteger
import java.net.InetAddress
import java.nio.ByteOrder

class WiFiTileService : TileService() {

    private val connectivityManager by lazy {
        applicationContext?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val wifiManager by lazy {
        applicationContext?.getSystemService(WIFI_SERVICE) as WifiManager
    }

    private val networkCallback by lazy {
        object : NetworkCallback() {

            override fun onAvailable(network: Network) {
                setActiveState()
            }

            override fun onLost(network: Network) {
                setInactiveState()
            }
        }
    }

    override fun onStartListening() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        if (wifiManager.isWifiEnabled) setActiveState() else setInactiveState()
    }

    override fun onClick() {
        if (isMinVersionQ()) {
            startActivityAndCollapse(Intent(ACTION_WIFI).addFlags(FLAG_ACTIVITY_NEW_TASK))
        } else {
            @Suppress("DEPRECATION") wifiManager.apply { isWifiEnabled = !isWifiEnabled }
        }
    }

    override fun onStopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun setActiveState() {
        updateTile(
            tileLabel = getWifiIpAddress(),
            tileState = STATE_ACTIVE
        )
    }

    private fun setInactiveState() {
        updateTile(
            tileLabel = getString(R.string.wifi_inactive_label),
            tileState = STATE_INACTIVE
        )
    }

    private fun updateTile(tileLabel: String, tileState: Int) {
        qsTile.apply {
            label = tileLabel
            state = tileState
            icon = getStateIcon(tileState)
            updateTile()
        }
    }

    private fun getStateIcon(state: Int) =
        Icon.createWithResource(
            this,
            if (state == STATE_ACTIVE) R.drawable.ic_wifi else R.drawable.ic_wifi_off
        )

    private fun getWifiIpAddress() =
        wifiManager.connectionInfo?.ipAddress?.let { ipAddress ->
            if (ipAddress > 0) {
                ipAddress.formatIpAddress() ?: getString(R.string.ip_address_not_found)
            } else {
                getString(R.string.ip_address_fetching)
            }
        } ?: getString(R.string.ip_address_not_found)

    private fun isMinVersionQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

private fun Int.formatIpAddress(): String? {
    val ipAddress =
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            Integer.reverseBytes(this)
        else {
            this
        }

    val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

    return InetAddress.getByAddress(ipByteArray)?.hostAddress
}
