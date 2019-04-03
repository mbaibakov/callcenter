package center.orbita.callcenter.servicestatus

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Ignore
import org.junit.Test
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.Date

@Ignore
class ServiceStatusRabbitPerformanceTest {

    @Test
    fun test() {
        val connectionFactory = CachingConnectionFactory("95.216.166.202", 5672)
        connectionFactory.username = "call_center"
        connectionFactory.setPassword("i4vtMx9nLEzv6ZhiUg2gXofo")
        connectionFactory.virtualHost = "call_center"
        val template = RabbitTemplate(connectionFactory)

        val mapper = ObjectMapper().registerModule(KotlinModule())

        for (i in 1..100) {
            val serviceStatusMessage = ServiceStatusMessage(serviceNumber = "service_number_$i",
                    phoneNumber = "+79991234567",
                    statusCode = 0,
                    creationDate = Date(),
                    releaseDate = Date(),
                    serviceName = "Тестовая услуга",
                    statusDescription = "",
                    number = "test-$i")
            val message = mapper.writeValueAsString(serviceStatusMessage)
            println(message)
            template.convertAndSend("call_center_in", message)
        }
    }
}