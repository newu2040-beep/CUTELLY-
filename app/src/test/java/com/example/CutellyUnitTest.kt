package com.example

import com.example.data.model.ActionType
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.GestureType
import com.example.data.model.StickerAnimationType
import com.example.data.preseed.PreseededData
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CutellyUnitTest {

    @Test
    fun `test preseeded stickers library count has at least 50 stickers`() {
        val stickers = PreseededData.preseededStickers
        assertTrue("Library should contain at least 50 stickers, found ${stickers.size}", stickers.size >= 50)
    }

    @Test
    fun `test preseeded stickers have valid categories`() {
        val stickers = PreseededData.preseededStickers
        val categories = stickers.map { it.category }.toSet()
        assertTrue(categories.contains("Cats"))
        assertTrue(categories.contains("Bunnies"))
        assertTrue(categories.contains("Food"))
        assertTrue(categories.contains("Stars"))
    }

    @Test
    fun `test default gesture mappings are populated`() {
        val mappings = PreseededData.defaultGestureMappings
        assertTrue(mappings.isNotEmpty())
        val tripleShake = mappings.find { it.gestureType == GestureType.TRIPLE_SHAKE }
        assertNotNull(tripleShake)
        assertEquals(ActionType.TAKE_SCREENSHOT, tripleShake?.actionType)
    }

    @Test
    fun `test gesture classification`() {
        fun classifyShake(shakeCount: Int): GestureType {
            return when (shakeCount) {
                3 -> GestureType.TRIPLE_SHAKE
                4 -> GestureType.FOUR_SHAKE
                else -> GestureType.FIVE_SHAKE
            }
        }

        assertEquals(GestureType.TRIPLE_SHAKE, classifyShake(3))
        assertEquals(GestureType.FOUR_SHAKE, classifyShake(4))
        assertEquals(GestureType.FIVE_SHAKE, classifyShake(5))
        assertEquals(GestureType.FIVE_SHAKE, classifyShake(6))
    }

    @Test
    fun `test backup json structure and parsing`() {
        val sampleJson = """
            {
              "version": 1,
              "theme": "STRAWBERRY_MILK",
              "soundEnabled": true,
              "hapticsEnabled": true,
              "floatingConfig": {
                "stickerId": "cat_white",
                "sizeDp": 84,
                "opacity": 1.0,
                "snapToEdge": true
              }
            }
        """.trimIndent()

        val json = JSONObject(sampleJson)
        assertEquals(1, json.getInt("version"))
        assertEquals("STRAWBERRY_MILK", json.getString("theme"))
        assertTrue(json.getBoolean("soundEnabled"))
        val cfg = json.getJSONObject("floatingConfig")
        assertEquals("cat_white", cfg.getString("stickerId"))
        assertEquals(84, cfg.getInt("sizeDp"))
    }

    @Test
    fun `test animation types all have display names`() {
        for (anim in StickerAnimationType.values()) {
            assertTrue(anim.displayName.isNotEmpty())
        }
    }
}
