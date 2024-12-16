package com.example.signaldetector.modules

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class NetworkData(
    private val activity: AppCompatActivity,
    private val networkDataTextView: TextView,
    private val dataLogger: DataLogger
) {
    private var callback: TelephonyCallback? = null
    private val tm by lazy { activity.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager }

    var rsrpLte = 0
    var rsrqLte = 0
    var asuLevelLte = 0
    var levelLte = 0
    var operatorLte: CharSequence = ""
    var mncLte = ""
    var mccLte = ""

    private inner class SignalStrengthCallback : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val infoList = tm.allCellInfo
            for ((i, cellInfo) in infoList.withIndex()) {
                if (cellInfo is CellInfoLte) {
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength
                    when (i) {
                        0 -> {
                            rsrpLte = cellSignalStrengthLte.rsrp
                            rsrqLte = cellSignalStrengthLte.rsrq
                            asuLevelLte = cellSignalStrengthLte.asuLevel
                            levelLte = cellSignalStrengthLte.level
                            operatorLte = tm.simOperatorName ?: ""
                            mncLte = tm.simOperator?.substring(3) ?: ""
                            mccLte = tm.simOperator?.substring(0, 3) ?: ""
                        }
                    }
                }
            }

            updateUI()
            sendSignalUpdate(rsrpLte, rsrqLte)
        }
    }

    private fun updateUI() {
        networkDataTextView.text = """
            LTE Signal Strength:
            RSRP: $rsrpLte dBm
            RSRQ: $rsrqLte dB
            ASU: $asuLevelLte
            Level: $levelLte
            Operator: $operatorLte
            MNC: $mncLte
            MCC: $mccLte
        """.trimIndent()

        dataLogger.updateDataLTE(rsrpLte, rsrqLte, asuLevelLte, levelLte, operatorLte.toString(), mncLte, mccLte)
    }

    fun getNetworkData() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        callback = SignalStrengthCallback()
        (callback as? SignalStrengthCallback)?.let {
            tm.registerTelephonyCallback(activity.mainExecutor, it)
        }
    }

    fun stopListening() {
        callback?.let {
            tm.unregisterTelephonyCallback(it)
        }
        dataLogger.stopLogging()
    }

    private fun sendSignalUpdate(rsrp: Int, rsrq: Int) {
        val intent = android.content.Intent("SIGNAL_UPDATE")
        intent.putExtra("RSRP", rsrp)
        intent.putExtra("RSRQ", rsrq)
        activity.sendBroadcast(intent)
    }
}

//package com.example.signaldetector.modules
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.telephony.*
//import android.widget.TextView
//import androidx.core.app.ActivityCompat
//import androidx.appcompat.app.AppCompatActivity
//
//class NetworkData(
//    private val activity: AppCompatActivity,
//    private val networkDataTextView: TextView,
//    private val dataLogger: DataLogger
//) {
//
//    private var phoneStateListener: PhoneStateListener? = null
//    private val tm: TelephonyManager by lazy {
//        activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//    }
//
//    private var rsrpLte: Int = 0
//    private var rsrqLte: Int = 0
//    private var asuLevelLte: Int = 0
//    private var levelLte: Int = 0
//    private var operatorLte: CharSequence? = null
//    private var mncLte: String? = ""
//    private var mccLte: String? = ""
//
//    private fun sendSignalUpdate(rsrp: Int, rsrq: Int) {
//        val intent = Intent("SIGNAL_UPDATE")
//        intent.putExtra("RSRP", rsrp)
//        intent.putExtra("RSRQ", rsrq)
//        activity.sendBroadcast(intent)
//    }
//
//    fun getNetworkData() {
//        phoneStateListener = object : PhoneStateListener() {
//            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
//                if (ActivityCompat.checkSelfPermission(
//                        activity,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//
//                val infoList = tm.allCellInfo
//                val cellSignalStrengths = signalStrength.cellSignalStrengths
//                val cellSignalStrength = cellSignalStrengths[0]
//
//                for (i in infoList.indices) {
//                    val cellInfo = infoList[i]
//
//                    if (cellInfo is CellInfoLte) {
//                        val cellSignalStrengthLte = if (i == 0) {
//                            cellSignalStrength as CellSignalStrengthLte
//                        } else {
//                            cellInfo.cellSignalStrength
//                        }
//                        val cellIdentityLte = cellInfo.cellIdentity
//
//                        rsrpLte = cellSignalStrengthLte.rsrp
//                        rsrqLte = cellSignalStrengthLte.rsrq
//                        asuLevelLte = cellSignalStrengthLte.asuLevel
//                        levelLte = cellSignalStrengthLte.level
//                        operatorLte = cellIdentityLte.operatorAlphaShort.toString()
//                        mncLte = cellIdentityLte.mncString
//                        mccLte = cellIdentityLte.mccString
//
//                        if (i == 0) {
//                            updateUI()
//                            sendSignalUpdate(rsrpLte, rsrqLte)
//                        }
//
//                        dataLogger.updateDataLTE(
//                            rsrpLte,
//                            rsrqLte,
//                            asuLevelLte,
//                            levelLte,
//                            operatorLte,
//                            mncLte,
//                            mccLte
//                        )
//                    }
//                }
//            }
//        }
//
//        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
//        dataLogger.startLogging()
//    }
//
//    private fun updateUI() {
//        val interfaceApp = buildString {
//            append("Technology: LTE\n\n\n")
//            append("RSRP: $rsrpLte\n\n")
//            append("RSRQ: $rsrqLte\n\n")
//            append("ASU Level: $asuLevelLte\n\n")
//            append("Level: $levelLte\n\n\n")
//            append("Operator: $operatorLte\n\n")
//            append("Mnc: $mncLte\n\n")
//            append("Mcc: $mccLte\n\n")
//        }
//        networkDataTextView.text = interfaceApp
//    }
//
//    fun stopListening() {
//        phoneStateListener?.let {
//            tm.listen(it, PhoneStateListener.LISTEN_NONE)
//        }
//        dataLogger.stopLogging()
//    }
//}
