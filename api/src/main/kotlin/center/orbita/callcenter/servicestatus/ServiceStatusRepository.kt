package center.orbita.callcenter.servicestatus

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface ServiceStatusRepository : CrudRepository<ServiceStatusEntity, Long> {

    fun getByPhoneNumberAndCreationDateGreaterThanEqual(phoneNumber: String, creationDate: Date): List<ServiceStatusEntity>

    fun getByNumber(number: String): ServiceStatusEntity?
}