package center.orbita.callcenter

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class CallCenterApplication

fun main(args: Array<String>) {
    SpringApplication.run(CallCenterApplication::class.java, *args)
}