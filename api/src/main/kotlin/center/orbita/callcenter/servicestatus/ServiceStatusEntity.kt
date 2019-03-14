package center.orbita.callcenter.servicestatus

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(name = "service_status", indexes = [Index(name = "service_status_creationDate", columnList = "creationDate"),
    Index(name = "service_status_number", columnList = "number")])
data class ServiceStatusEntity(
    @Column @Id @GeneratedValue val id: Long? = null,
    @Column val number: String? = null,
    @Column val phoneNumber: String? = null,
    @Column val serviceName: String? = null,
    @Column val statusCode: Int? = null,
    @Column val statusDescription: String? = null,
    @Column val releaseDate: Date? = null,
    @Column val creationDate: Date? = null)