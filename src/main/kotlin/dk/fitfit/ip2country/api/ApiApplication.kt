package dk.fitfit.ip2country.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}

@RestController
class Ip2CountryController {
    private val logger: Logger = LoggerFactory.getLogger(Ip2CountryController::class.java)

    @GetMapping("/ip2country/{ip}")
    fun ip2country(@PathVariable ip: String): String {
        logger.info("/ip2country/$ip")
        val locale: Locale = ip2locale(ip)
        return locale.country
    }

    private fun ip2locale(ip: String): Locale {
        val output = cmd(listOf("geoiplookup", ip))
        val country = output.substring(output.lastIndexOf(": ") + 1)
        val countryCode = country.split(", ")[0].trim()
        return Locale("", countryCode)
    }

    private fun cmd(command: List<String>): String {
        val process = ProcessBuilder(command).start()
        return readStream(process.inputStream)
    }

    private fun readStream(stream: InputStream): String {
        Scanner(stream, StandardCharsets.UTF_8.toString()).use({ scanner ->
            scanner.useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else ""
        })
    }

}
