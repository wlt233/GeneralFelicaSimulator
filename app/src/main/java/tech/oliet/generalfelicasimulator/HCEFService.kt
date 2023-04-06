package tech.oliet.generalfelicasimulator

import android.nfc.cardemulation.HostNfcFService
import android.os.Bundle
import android.util.Log
import android.widget.Toast


class HCEFService : HostNfcFService() {
    private var card = FelicaCard()

    // byte utils
    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
    fun ByteArray.toHexString(hasSpace: Boolean = true) = this.joinToString("") {
        (it.toInt() and 0xFF).toString(16).padStart(2, '0').uppercase() + if (hasSpace) " " else ""
    }


    // resp utils
    fun packResponse(respType: Byte, nfcid2: ByteArray, payload: ByteArray): ByteArray {
        var resp = ByteArray(1) + respType + nfcid2 + payload
        resp[0] = resp.size.toByte()
        return resp
    }

    override fun processNfcFPacket(commandPacket: ByteArray, extras: Bundle?): ByteArray? {
        val commandHexStr = commandPacket.toHexString()
        Toast.makeText(this, "received $commandHexStr", Toast.LENGTH_LONG).show()
        Log.d("HCEFService", "processNfcFPacket NFCF")
        Log.d("HCEFService", commandHexStr)

        if (commandPacket.size < 1 + 1 + 8 || (commandPacket.size.toByte() != commandPacket[0])) {
            Log.e("HCEFService", "processNfcFPacket: packet size error")
            return null
        }

        val nfcid2 = ByteArray(8)
        System.arraycopy(commandPacket, 2, nfcid2, 0, 8)
//        val myNfcid2 =
//            byteArrayOfInts(0x02, 0xFE, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
//        if (!Arrays.equals(myNfcid2, nfcid2)) {
//            Log.e("HCEFService", "processNfcFPacket: nfcid2 error")
//            return null
//        }

        if (commandPacket[1] === 0x06.toByte()) { // READ BLK
            var payload = ByteArray(0)
            return packResponse(0x07, nfcid2, payload)
        }
        else if (commandPacket[1] === 0x08.toByte()) { // WRITE BLK
            var payload = ByteArray(0)
            return packResponse(0x09, nfcid2, payload)
        }

       return byteArrayOfInts(0x04, 0x11, 0x45, 0x14)
       // sendResponsePacket(byteArrayOfInts(0x04, 0x11, 0x45, 0x14))
       // return null
    }


    // this class will be created for each received command
    // so card json need to be loaded every time
    override fun onCreate() {
        Log.d("HCEFService", "onCreate NFCF")
        super.onCreate()
        card.fromJson("path_to_json")
        // Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        Log.d("HCEFService", "onDestroy NFCF")
        super.onDestroy()
        // Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show()
    }
    override fun onDeactivated(reason: Int) {
        Log.d("HCEFService", "onDeactivated NFCF")
        // Toast.makeText(this, "onDeactivated", Toast.LENGTH_LONG).show()
    }
}