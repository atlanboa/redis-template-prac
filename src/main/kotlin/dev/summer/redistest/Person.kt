package dev.summer.redistest

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("person")
class Person(
    @Id val id: String,
    val name: String,
    val age: Int
)