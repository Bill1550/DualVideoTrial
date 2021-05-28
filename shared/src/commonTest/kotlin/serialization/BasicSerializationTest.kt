package serialization

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicSerializationTest {

    @Serializable
    private data class Primitives(
        val boolean: Boolean,
        val int: Int,
        val long: Long,
        val string: String,
        val float: Float,
        val double: Double
    )

    @Serializable
    private data class SomeDefaults(
        val int1: Int = 42,
        val int2: Int,

        @Required
        val s1:String ="Hello"
    )

    val fullSerializer by lazy { Json { encodeDefaults = true } }

    @Test
    fun helloCheck() {
        println("Hello")
    }

    /**
     * Verify that the primitive types encode/decode properly
     */
    @Test
    fun serializePrimitives() {

        val p1 = Primitives(
            boolean =  true,
            int = 123,
            long = 123456789,
            string = "Some string data",
            float = 1.1f,
            double = 2.2
        )

        val p1Expected = """{"boolean":true,"int":123,"long":123456789,"string":"Some string data","float":1.1,"double":2.2}"""

        val p1json = Json.encodeToString(Primitives.serializer(), p1)

        println( "'$p1json'")
        assertEquals( p1Expected, p1json )
    }

    @Test
    fun serializeDefaults() {
        val d1 = SomeDefaults( int2=99)

        // Default serializer behavior is not to encoded defaulted valeus
        val d1Json = Json.encodeToString( SomeDefaults.serializer(), d1 )
        val d1ExpectedJson = """{"int2":99,"s1":"Hello"}"""
        println( "Encoded w/ defaults: $d1Json" )
        assertEquals(d1ExpectedJson, d1Json)

        val d1Decoded = Json.decodeFromString( SomeDefaults.serializer(), d1ExpectedJson )
        println( "Decoded: $d1Decoded" )
        assertEquals( d1, d1Decoded )

        // Encoding default values requires an encoder option
        val d1FullJson = Json { encodeDefaults = true }.encodeToString( SomeDefaults.serializer(), d1 )
        val d1ExpectedFullJson = """{"int1":42,"int2":99,"s1":"Hello"}"""
        println( "Encoded w/o defaults: $d1FullJson" )
        assertEquals( d1ExpectedFullJson, d1FullJson )

    }

}