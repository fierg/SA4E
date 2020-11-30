import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.net.InetSocketAddress
import java.util.concurrent.Executors


@KtorExperimentalAPI
fun main(args: Array<String>) {
    runBlocking {
        val exec = Executors.newCachedThreadPool()
        val jobs = mutableSetOf<JobStat>()
        val RGX_JOB = Regex("\\(\\\"count\\\",(\\d+),(\\d+)\\)")
        val RGX_STAT = Regex("\\(\\\"stats\\\"\\)")
        val server =
            aSocket(ActorSelectorManager(exec.asCoroutineDispatcher())).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
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
                        //output.writeStringUtf8("$line\r\n")

                        if (RGX_JOB.matches(line.toString())) {
                            val groups = RGX_JOB.find(line.toString())!!.groupValues
                            output.writeStringUtf8("Calculating primes in range ${groups[1]} to ${groups[2]}...\r\n")
                            val startTime = System.nanoTime();
                            val primes = getPrimesInRange(groups[1].toInt(), groups[2].toInt())
                            val endTime = System.nanoTime();

                            output.writeStringUtf8("${primes}\r\n")
                            jobs.add(JobStat(groups[1].toInt(), groups[2].toInt(), primes, (endTime - startTime)))
                        } else if (RGX_STAT.matches(line.toString())){
                            jobs.forEach { 
                                output.writeStringUtf8(it.toString())
                            }
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                } finally {
                    socket.close()
                }
            }
        }
    }
}

fun getPrimesInRange(start: Int, end: Int): Int {
    var primes = 0
    for (i in start..end) {
        if (isPrime(i)) primes++
    }
    return primes
}


// Given a positive number n, find the largest number m such
// that 2^m divides n.
private fun val2(n: Int): Int {
    var n = n
    var m = 0
    if (n and 0xffff == 0) {
        n = n shr 16
        m += 16
    }
    if (n and 0xff == 0) {
        n = n shr 8
        m += 8
    }
    if (n and 0xf == 0) {
        n = n shr 4
        m += 4
    }
    if (n and 0x3 == 0) {
        n = n shr 2
        m += 2
    }
    if (n > 1) {
        m++
    }
    return m
}

// For convenience, handle modular exponentiation via BigInteger.
private fun modPow(base: Int, exponent: Int, m: Int): Int {
    val bigB = BigInteger.valueOf(base.toLong())
    val bigE = BigInteger.valueOf(exponent.toLong())
    val bigM = BigInteger.valueOf(m.toLong())
    val bigR = bigB.modPow(bigE, bigM)
    return bigR.toInt()
}

// Basic implementation.
private fun isStrongProbablePrime(n: Int, base: Int): Boolean {
    val s = val2(n - 1)
    var d = modPow(base, n shr s, n)
    if (d == 1) {
        return true
    }
    for (i in 1 until s) {
        if (d + 1 == n) {
            return true
        }
        d = d * d % n
    }
    return d + 1 == n
}

fun isPrime(n: Int): Boolean {
    if (n and 1 == 0) {
        return n == 2
    }
    return if (n < 9) {
        n > 1
    } else isStrongProbablePrime(n, 2) && isStrongProbablePrime(n, 7) && isStrongProbablePrime(n, 61)
}
