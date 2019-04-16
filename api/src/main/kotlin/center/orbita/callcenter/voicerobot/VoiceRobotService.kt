package center.orbita.callcenter.voicerobot

import center.orbita.callcenter.corda.service.RequestService
import center.orbita.callcenter.servicestatus.ServiceStatusRepository
import center.orbita.callcenter.structure.RequestModel
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Calendar
import java.util.Date

const val GetApplicationsByMsisdnMethod = "getApplicationsByMsisdn"
const val DEFAULT_DAY_PERIOD = 31 // 31 - days (1 month)

@Service
class VoiceRobotService(private val serviceStatusRepository: ServiceStatusRepository,
    private val requestService: RequestService) {

    companion object {
        val logger = LoggerFactory.getLogger(VoiceRobotService::class.java)!!
        val mapper = ObjectMapper().registerModule(KotlinModule())!!
    }

    fun processRequest(request: VoiceRobotRequest): VoiceRobotResponse {
        val response = innerProcessRequest(request)
        asyncWriteRequestToCorda(request, response)
        return response
    }

    private fun innerProcessRequest(request: VoiceRobotRequest): VoiceRobotResponse {
        if (request.data.method == GetApplicationsByMsisdnMethod) {

            if (!isMsisdnCorrect(request.data.msisdn)) return errorResponse(2, "Неправильный формат msisdn")

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -DEFAULT_DAY_PERIOD)
            val creationDate = calendar.time
            val serviceStatuses = serviceStatusRepository.getByPhoneNumberAndCreationDateGreaterThanEqual(normalizePhoneNUmber(request.data.msisdn), creationDate)

            if (serviceStatuses.isEmpty()) return errorResponse(3, "Ни одно заявление не найдено")

            return VoiceRobotResponse(data = VoiceRobotResponseApplicationsData(serviceStatuses.map {
                ApplicationData(number = it.number!!,
                        status = it.statusCode!!,
                        createDate = it.creationDate!!,
                        plannedDate = it.releaseDate!!,
                        statusComment = it.statusDescription)
            }))
        } else {
            return errorResponse(1, "Неверный формат запроса", "Метод ${request.data.method} не поддерживается")
        }
    }

    private fun isMsisdnCorrect(msisdn: String)= msisdn.matches(Regex("^(\\+7)+[0-9]{10}"))

    private fun normalizePhoneNUmber(msisdn: String) = msisdn.substring(2)

    private fun errorResponse(code: Int, title: String, detail: String? = null) =
            VoiceRobotResponse(errors = listOf(VoiceRobotResponseError(code, title, detail)))

    private fun asyncWriteRequestToCorda(request: VoiceRobotRequest, response: VoiceRobotResponse) {
        GlobalScope.async {
            val id = requestService.create(RequestModel(msisdn = request.data.msisdn,
                    creationDate = Date(),
                    responseData = mapper.writeValueAsString(response)))
            logger.info("Request $id created")
        }
    }
}