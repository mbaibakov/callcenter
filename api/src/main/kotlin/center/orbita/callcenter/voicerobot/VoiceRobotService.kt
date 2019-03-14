package center.orbita.callcenter.voicerobot

import center.orbita.callcenter.servicestatus.ServiceStatusRepository
import org.springframework.stereotype.Service
import java.util.Calendar

const val GetApplicationsByMsisdnMethod = "getApplicationsByMsisdn"
const val DEFAULT_DAY_PERIOD = 7 // 7 - days

@Service
class VoiceRobotService(val serviceStatusRepository: ServiceStatusRepository){

    fun processRequest(request: VoiceRobotRequest) : VoiceRobotResponse {
        if (request.data.method == GetApplicationsByMsisdnMethod){
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, - DEFAULT_DAY_PERIOD)
            val creationDate = calendar.time
            val serviceStatuses = serviceStatusRepository.getByPhoneNumberAndCreationDateGreaterThanEqual(normalizePhoneNUmber(request.data.msisdn), creationDate)

            if (serviceStatuses.isEmpty()) return  errorResponse(3, "Ни одно заявление не найдено")

            return VoiceRobotResponse(data = VoiceRobotResponseApplicationsData(serviceStatuses.map {
                ApplicationData(number = it.phoneNumber!!,
                        status = it.statusCode!!,
                        createDate = it.creationDate!!,
                        plannedDate = it.releaseDate!!,
                        statusComment = it.statusDescription ) }))
        } else {
            return errorResponse(1, "Неверный формат запроса", "Метод ${request.data.method} не поддерживается")
        }
    }

    private fun normalizePhoneNUmber(msisdn: String) = msisdn.substring(2)

    private fun errorResponse(code: Int, title: String, detail: String? = null) =
            VoiceRobotResponse(errors = listOf(VoiceRobotResponseError(code, title, detail)))

}