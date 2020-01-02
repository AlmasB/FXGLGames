package com.almasb.fxglgames.ncc

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EditorApp : GameApplication() {

    override fun initSettings(settings: GameSettings) {

    }
}

/**
 *                     ,

Skill(
"Lightning Strike",
"Description ...",
"imageName",
TargetType.ENEMY,
0,
3,
0,
0,
15, 0, 0, 0
)
 */

fun main() {
    val card = Card(
            "Orc Warrior",
            "A basic attack grunt",
            "imageName",
            Rarity.COMMON,
            CardType.WARRIOR,
            Element.EARTH,

            arrayListOf(
                    Skill(
                            "Lightning Bolt",
                            "Description ...",
                            "imageName",
                            TargetType.ENEMY,
                            0,
                            3,
                            0,
                            0,
                            15, 0, 0, 0
                    )
            ),

            1,
            10,
            3,
            4,
            2
    )


    val deck = Deck(
            listOf(
                    card
            )
    )



    val mapper = jacksonObjectMapper()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    val json = mapper.writeValueAsString(deck)

    println("JSON card:\n" + json)

    val deck2 = mapper.readValue(json, Deck::class.java)

    println("JVM card:\n" + deck2)

    //GameApplication.launch(EditorApp::class.java, emptyArray())
}