package center.orbita.callcenter.voicerobot

data class VoiceRobotRequest(
    val data: VoiceRobotRequestData
)

data class VoiceRobotRequestData(
    val method: String,
    val msisdn: String
)