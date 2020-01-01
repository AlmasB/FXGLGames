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

fun main() {
    val card = Card(
            "Name",
            "Description",
            "imageName",
            Rarity.COMMON,
            CardType.WARRIOR,
            Element.EARTH,

            arrayListOf(
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
                    ),

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
            ),

            1,
            10,
            3,
            4,
            2
    )

    val mapper = jacksonObjectMapper()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    val json = mapper.writeValueAsString(card)

    println("JSON card:\n" + json)

    val card2 = mapper.readValue(json, Card::class.java)

    println("JVM card:\n" + card2)

    //GameApplication.launch(EditorApp::class.java, emptyArray())
}