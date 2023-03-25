package dev.summer.redistest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class PersonServiceTest{
    @Autowired
    private lateinit var personService: PersonService

    @Test
    fun `test person service`() {
        val person = Person(UUID.randomUUID().toString(), "summer", 31)
        val savedPerson = personService.addPerson(person)
        assertEquals(person.id, savedPerson.id)
        assertEquals(person.name, savedPerson.name)
        assertEquals(person.age, savedPerson.age)

        val getPerson = personService.getPerson(person.id)
        assertEquals(person.id, getPerson.id)
        assertEquals(person.name, getPerson.name)
        assertEquals(person.age, getPerson.age)
    }
}