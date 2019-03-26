package center.orbita.callcenter.servicestatus

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class ServiceStatusMessage(
    @JsonProperty("phone_number")
    val phoneNumber: String,

    @JsonProperty("number_target")
    val number: String,

    @JsonProperty("name_gu")
    val serviceName: String,

    @JsonProperty("status_code")
    val statusCode: Int,

    @JsonProperty("status_desc")
    val statusDescription: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("output_target_dt")
    val releaseDate: Date,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("create_target_dt")
    val creationDate: Date
) {
    fun convertToEntity() = ServiceStatusEntity(
            phoneNumber = this.phoneNumber,
            number = this.number,
            serviceName = this.serviceName,
            statusCode = this.statusCode,
            statusDescription = this.statusDescription,
            releaseDate = this.releaseDate,
            creationDate = this.creationDate
    )
}