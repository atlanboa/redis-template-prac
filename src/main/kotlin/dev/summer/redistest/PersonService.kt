package dev.summer.redistest

import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personRepository: PersonRepository
) {

    fun addPerson(person: Person): Person {
        return personRepository.save(person)
    }

    fun getPerson(id: String): Person {
        return personRepository.findById(id).orElseThrow { throw RuntimeException() }
    }


}