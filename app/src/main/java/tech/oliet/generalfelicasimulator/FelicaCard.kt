package tech.oliet.generalfelicasimulator

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class FelicaCard(jsonPath: String) {
    private var cardData = mutableMapOf<String, String>()

    private val gson = Gson()
    private var jsonFile: File

    init {
        Log.d("HCEFService", "Load Felica Data from $jsonPath")
        jsonFile = File(jsonPath)
        val mutableBlock = object : TypeToken<MutableMap<String, String>>() {}.type
        try {
            val jsonData = gson.fromJson<MutableMap<String, String>>(jsonFile.readText(), mutableBlock)
            if (jsonData != null) {
                cardData = jsonData
            }
            Log.d("HCEFService","Felica Card Data")
            Log.d("HCEFService","=".repeat(53))
            for (en in cardData.entries) {
                Log.d("HCEFService","[${en.key}]: ${en.value}")
            }
            Log.d("HCEFService","=".repeat(53))
        } catch (e: IOException) {
            Log.e("Error", "File Read Error")
        } catch (e: JsonSyntaxException) {
            Log.e("Error", "File Syntax Error")
        }
    }

    // utils
    fun String.decodeHex(): ByteArray =
        this.replace(" ", "").chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    fun readBlock(id: Byte): ByteArray? {
        var idStr = (id.toInt() and 0xFF).toString(16).padStart(2, '0')
        Log.d("HCEFService", "read block [$idStr]")
        var blockStr = cardData[idStr]
        if (blockStr == null) {
            idStr = idStr.uppercase()
            blockStr = cardData[idStr]
        }
        if (blockStr != null) {
            val blockData = blockStr.decodeHex()
            val resp = ByteArray(16)
            System.arraycopy(blockData, 0, resp, 0, blockData.size)
            return resp
        }

        Log.e("Error", "Invalid Block")
        return null
    }

    fun writeBlock(id: Byte, data: ByteArray) { }
}