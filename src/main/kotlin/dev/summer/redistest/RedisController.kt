package dev.summer.redistest

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisController(
    private val stringRedisTemplate: StringRedisTemplate,
    private val personService: PersonService
) {
    @GetMapping("/redis")
    fun redis(): String {
        val opsForValue = stringRedisTemplate.opsForValue()
        opsForValue["name"] = "myName"
        return "saved"
    }

    @GetMapping("/person/{id}")
    fun getPerson(@PathVariable id: String): Person {
        return personService.getPerson(id)
    }

    @PostMapping("/person")
    fun addPerson(@RequestBody person: Person): Person {
        return personService.addPerson(person)
    }
}