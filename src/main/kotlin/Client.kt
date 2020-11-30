import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.InetSocketAddress

fun main(args: Array<String>) {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)


        output.writeStringUtf8("(\"count\",1,500)\r\n")
        sleep(3000)

        var response = input.readUTF8Line()
        println("Server said: '$response'")

        sleep(3000)

        response = input.readUTF8Line()
        println("Server said: '$response'")


        //socket.close()
    }
}
