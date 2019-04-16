package center.orbita.callcenter.corda.service

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/requests")
class RequestController(private val requestService: RequestService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun list() = requestService.list()

    @GetMapping("/{number}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByPhoneNumber(@PathVariable("number") number: String) = requestService.getByPhoneNumber(number)
}