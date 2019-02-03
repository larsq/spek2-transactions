package com.github.larsq.spektx.model

import javax.persistence.*

@Entity
@Table(name = "Heroes")
class Hero() {
    constructor(name: String, city: String) : this() {
        this.name = name
        this.city = city
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(name = "name", unique = true)
    lateinit var name: String

    @Column(name = "age")
    lateinit var city: String
}