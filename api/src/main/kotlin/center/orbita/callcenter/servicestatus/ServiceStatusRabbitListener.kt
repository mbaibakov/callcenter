package center.orbita.callcenter.servicestatus

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ServiceStatusRabbitListener(val repository: ServiceStatusRepository) {

    companion object {
        val logger = LoggerFactory.getLogger(ServiceStatusRabbitListener::class.java)!!
        val mapper = ObjectMapper()
    }

    @RabbitListener(queues = ["call_center_in"])
    fun processMessage(message: Message) {
        val body = String(message.body)
        logger.info("Incoming message $body")
        try {
            val msg = mapper.readValue(body, ServiceStatusMessage::class.java)
            val existEntity = repository.getByNumber(msg.number)
            val newEntity = existEntity?.copy(statusCode = msg.statusCode,
                    statusDescription = msg.statusDescription,
                    creationDate = msg.creationDate,
                    releaseDate = msg.releaseDate)
                    ?: msg.convertToEntity()
            repository.save(newEntity)
        } catch (e: IOException){
            logger.error(e.localizedMessage, e)
        }
    }
}