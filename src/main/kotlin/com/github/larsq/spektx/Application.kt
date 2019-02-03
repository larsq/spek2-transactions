package com.github.larsq.spektx
import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.github.larsq.spektx")
                .mainClass(javaClass)
                .start()
    }
}