package center.orbita.callcenter.voicerobot

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VoiceRobotResponse(
    val data: VoiceRobotResponseData? = null,
    val errors: List<VoiceRobotResponseError>? = null
)

interface VoiceRobotResponseData

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VoiceRobotResponseError(
    val code: Int,
    val title: String,
    val detail: String? = null
)

data class VoiceRobotResponseApplicationsData(
    val applications: List<ApplicationData>
) : VoiceRobotResponseData

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationData(
    val number: String,
    val status: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    val createDate: Date,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    val plannedDate: Date,
    val statusComment: String? = null
)