package tech.oliet.generalfelicasimulator

class FelicaCard {
    private var cardData = mutableMapOf<Int, ByteArray>()

    public fun fromJson(cardjson: String) { }

    public fun readBlock(blk: Int): ByteArray? = null

    public fun writeBlock(blk: Int, data: ByteArray) { }
}