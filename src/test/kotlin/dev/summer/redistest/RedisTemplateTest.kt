package dev.summer.redistest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.ListOperations.MoveFrom
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.UUID

@SpringBootTest
internal class RedisTemplateTest {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Test
    fun `test string redis template`() {
        val opsForValue = redisTemplate.opsForValue()
        opsForValue["count"] = 1

        val get = opsForValue.get("count")
        assertEquals(1, get)

        opsForValue.decrement("count")
        val get2 = opsForValue.get("count")
        assertEquals(0, get2)

        opsForValue.increment("count")
        val get3 = opsForValue.get("count")
        assertEquals(1, get3)

        opsForValue.set("age", 31)
        val get4 = opsForValue.get("age")
        assertEquals(31, get4)

        val multiGet = opsForValue.multiGet(listOf("count", "age"))
        assertEquals(2, multiGet?.size ?: 0)
    }

    @Test
    fun `timeout test`() {
        val opsForValue = redisTemplate.opsForValue()

        opsForValue.set("timeout", "timeout", Duration.ofSeconds(3))
        val get1 = opsForValue.get("timeout")
        assertEquals("timeout", get1)

        Thread.sleep(3000L)

        val get5 = opsForValue.get("timeout")
        assertNull(get5)
    }

    @Test
    fun `bitmap test`() {
        val key = "visit"
        redisTemplate.delete(key)
        val opsForValue = redisTemplate.opsForValue()
        opsForValue.setBit(key, 0, true)
        val execute = redisTemplate.execute { connection -> connection.bitCount("visit".toByteArray()) }
        assertEquals(1, execute)
        opsForValue.setBit(key, 1, true)
        val execute1 = redisTemplate.execute { connection -> connection.bitCount("visit".toByteArray()) }
        assertEquals(2, execute1)
    }

    @Test
    fun `opsForList test`(){
        val key = "listKey"
        val desKey = "destination"
        val nonKey = "nonKey"
        val opsForList = redisTemplate.opsForList()

        redisTemplate.delete(key)
        redisTemplate.delete(desKey)
        redisTemplate.delete(nonKey)

        opsForList.rightPush(key, "a")
        val index0 = opsForList.index(key, 0)
        assertEquals("a", index0)

        opsForList.leftPush(key, "b")
        val index1 = opsForList.index(key, 0)
        assertEquals("b", index1)

        val leftPop = opsForList.leftPop(key)
        assertEquals("b", leftPop)

        val rightPop = opsForList.rightPop(key)
        assertEquals("a", rightPop)

        val lastIndexOf = opsForList.lastIndexOf(key, "a")
        assertEquals(null, lastIndexOf)

        opsForList.rightPushAll(key, "a", "b", "c")
        val lastIndexOf1 = opsForList.lastIndexOf(key, "a")
        assertEquals(0, lastIndexOf1)

        opsForList.rightPushAll(key, "a", "b", "c")
        opsForList.remove(key, 2, "a")
        val lastIndexOf2 = opsForList.lastIndexOf(key, "a")
        assertEquals(null, lastIndexOf2)

        opsForList.move(MoveFrom.fromHead(key), ListOperations.MoveTo.toTail(desKey))
        val index = opsForList.index(desKey, 0)
        assertEquals("b", index)

        opsForList.set(desKey, 0, "a")
        val setResult = opsForList.index(desKey, 0)
        assertEquals("a", setResult)

        val leftPushIfPresent = opsForList.leftPushIfPresent(nonKey, "b")
        assertEquals(0, leftPushIfPresent)

        val range = opsForList.range(key, 0, 3)
        assertEquals(3, range?.size ?: 0)
    }

    @Test
    fun `test opsForHash`() {
        val key = "key"
        val hashKey = "hashKey"
        val opsForHash = redisTemplate.opsForHash<String, String>()

        opsForHash.put(key, hashKey, "value")
        val result1 = opsForHash.get(key, hashKey)
        assertEquals("value", result1)
    }

    @Test
    fun `test obj save`(){
        val uuid = UUID.randomUUID()
        val user = User(uuid, "name")
        val opsForValue = redisTemplate.opsForValue()
        opsForValue.set(user.id.toString(), user)

        println(uuid)

        val get = opsForValue.get(uuid.toString()) as LinkedHashMap<*, *>

        val any = get["id"]
        println(any)
    }

    data class User(
        val id: UUID,
        val name: String
    )

    @Test
    fun `test string redis template 2`() {
        val opsForCluster = redisTemplate.opsForCluster()
        val opsForGeo = redisTemplate.opsForGeo()
        val opsForHash = redisTemplate.opsForHash<String, String>()
        val opsForList = redisTemplate.opsForList()
        val opsForValue = redisTemplate.opsForValue()
        opsForValue["name"] = "myName"
    }
}