package wtf.meier.tariff.api.repository.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import wtf.meier.tariff.interpreter.model.tariff.TariffId

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class TariffNotFoundException(tariffId: TariffId) : RuntimeException("tariff with id $tariffId wasn't found")