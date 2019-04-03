package center.orbita.callcenter.servicestatus

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(name = "service_status", indexes = [Index(name = "service_status_creationDate", columnList = "creationDate")])
data class ServiceStatusEntity(
    @Column @Id val id: String? = null,
    @Column val number: String? = null,
    @Column val phoneNumber: String? = null,
    @Column val serviceName: String? = null,
    @Column val statusCode: Int? = null,
    @Column val statusDescription: String? = null,
    @Column val releaseDate: Date? = null,
    @Column val creationDate: Date? = null)