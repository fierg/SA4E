import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.InetSocketAddress

fun main(args: Array<String>) {

    launchPrimeCalculation(1,500)
    launchPrimeCalculation(500,10000)
    launchPrimeCalculation(10000,100000)
    launchPrimeCalculation(100000,500000)

    sleep(30000)

    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)

        output.writeStringUtf8("(\"stats\")\r\n")
        val response = input.readUTF8Line()
        println("Server said: '$response'")

        socket.awaitClosed()
    }


}

private fun launchPrimeCalculation(from: Int, to: Int) {
    GlobalScope.launch {
        runBlocking {
            val socket =
                aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            handleCount(from, to, output, input)
            socket.awaitClosed()
        }
    }
}

suspend fun handleCount(from: Int, to: Int, output: ByteWriteChannel, input: ByteReadChannel) {
    output.writeStringUtf8("(\"count\",$from,$to)\r\n")
    val response = input.readUTF8Line()
    println("Server said: '$response'")
}
