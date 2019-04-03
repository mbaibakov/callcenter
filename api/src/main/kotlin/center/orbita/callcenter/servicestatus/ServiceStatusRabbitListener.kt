package center.orbita.callcenter.servicestatus

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ServiceStatusRabbitListener(private val repository: ServiceStatusRepository) {

    companion object {
        val logger = LoggerFactory.getLogger(ServiceStatusRabbitListener::class.java)!!
        val mapper = ObjectMapper()
    }

    @RabbitListener(queues = ["call_center_in"])
    fun listener1(message: Message) = processMessage(message)

    @RabbitListener(queues = ["call_center_in"])
    fun listener2(message: Message) = processMessage(message)

    @RabbitListener(queues = ["call_center_in"])
    fun listener3(message: Message) = processMessage(message)

    private fun processMessage(message: Message) {
        val body = String(message.body)
        logger.info("Incoming message $body")
        try {
            val msg = mapper.readValue(body, ServiceStatusMessage::class.java)
            logger.debug("Incoming object $msg")
            val entity =  msg.convertToEntity()
            logger.info("Entity $entity")
            repository.save(entity)
        } catch (e: IOException) {
            logger.error(e.localizedMessage, e)
        }
    }
}