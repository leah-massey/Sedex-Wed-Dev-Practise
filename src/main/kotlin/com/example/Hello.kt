package com.example

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val app: HttpHandler = routes(
    "{lang:[a-zA-Z-]+}/hello" bind GET to { req: Request ->

        val lang: String = req.path("lang") ?: "en-US"
        val name: String = req.query("name") ?: ""

        val greeting = when (lang) {
            "en-US" -> "Hello"
            "fr-FR" -> "Bonjour"
            "en-AU" -> "G'day"
            "it-IT" -> "Ciao"
            "en-GB" -> "Good day"
            else -> "Hello"
        }

        if (name == "") {
            Response(OK).body("$greeting")
        } else if (name.matches(Regex("[a-zA-Z]+"))) {
            // Name is a string of characters (alphabetic) or not declared
            Response(OK).body("$greeting $name")
        } else {
            Response(BAD_REQUEST).body("Invalid name")
        }
    }
    ,

    "/echo_headers" bind GET to {req: Request ->
        val headers = req.headers
        val headersAsList = headers.map{"${it.first}: ${it.second}"}.joinToString("\n")

        println("these are HEADERS $headers")
        Response(OK).body("$headersAsList")
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(3000)).start()

    println("Server started on " + server.port())
}
