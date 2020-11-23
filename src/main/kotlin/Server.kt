import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.util.concurrent.Executors


@KtorExperimentalAPI
fun main(args: Array<String>) {
    runBlocking {
        val exec = Executors.newCachedThreadPool()

        val server = aSocket(ActorSelectorManager(exec.asCoroutineDispatcher())).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started echo tcp server at ${server.localAddress}")

        while (true) {
            val socket = server.accept()

            launch {
                println("Socket accepted: ${socket.remoteAddress}")

                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                try {
                    while (true) {
                        val line = input.readUTF8Line()

                        println("${socket.remoteAddress}: $line")
                        output.writeStringUtf8("$line\r\n")
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }
    }
}