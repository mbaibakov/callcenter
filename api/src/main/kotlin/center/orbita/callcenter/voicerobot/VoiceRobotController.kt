package center.orbita.callcenter.voicerobot

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/voice-robot")
class VoiceRobotController(private val voiceRobotService: VoiceRobotService) {

    companion object {
        val logger = LoggerFactory.getLogger(VoiceRobotController::class.java)!!
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun processRequest(@RequestBody voiceRobotRequest: VoiceRobotRequest): VoiceRobotResponse {
        logger.info("Incoming request  $voiceRobotRequest")
        return voiceRobotService.processRequest(voiceRobotRequest)
    }
}