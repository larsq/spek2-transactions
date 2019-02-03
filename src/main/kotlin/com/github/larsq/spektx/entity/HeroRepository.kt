package com.github.larsq.spektx.entity

import com.github.larsq.spektx.model.Hero
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface HeroRepository {
    fun save(hero: Hero)
    fun findByName(name: String): Hero?
    fun deleteAll()
}

@Singleton
class JpaHeroRepository(@CurrentSession @PersistenceContext private val entityManager: EntityManager) :
    HeroRepository {
    override fun findByName(name: String): Hero? =
        entityManager.createQuery("select h from Hero h where name = :name", Hero::class.java)
            .setParameter("name", name).resultList.firstOrNull()

    override fun save(hero: Hero) {
        entityManager.persist(hero)
    }

    override fun deleteAll() {
        entityManager.createQuery("DELETE from Hero").executeUpdate()
    }
}