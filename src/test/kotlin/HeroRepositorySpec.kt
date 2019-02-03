package com.github.larsq.spektx.model

import com.github.larsq.spektx.entity.HeroRepository
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.xdescribe
import support.tx
import javax.persistence.EntityManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object HeroRepositorySpec : Spek({
    tx {
        describe("deleteAll") {
            val em = entityManager
            val heroRepository: HeroRepository = applicationContext.getBean(HeroRepository::class.java)

            beforeEach {
                entityManager.persist(Hero("Marvel", "New York"))
            }

            it("should remove all heroes") {
                //do
                heroRepository.deleteAll()

                //assert
                assertEquals(
                    0, em.createQuery(
                        "SELECT h from Hero as h",
                        Hero::class.java
                    ).resultList.size
                )
            }
        }

        describe("findByName") {
            val heroRepository: HeroRepository = applicationContext.getBean(HeroRepository::class.java)

            beforeEach {
                entityManager.persist(Hero("Dare Devil", "Chicago"))
            }

            afterEach {
                entityManager.createQuery("DELETE from Hero").executeUpdate()
            }

            it("should return specified hero") {
                assertNotNull(heroRepository.findByName("Dare Devil"))
            }
        }

        describe("save") {
            val heroRepository: HeroRepository = applicationContext.getBean(HeroRepository::class.java)
            val em: EntityManager = entityManager

            beforeEach {
                heroRepository.save(Hero("Thor", "Aasgard"))
            }

            afterEach {
                entityManager.createQuery("DELETE from Hero").executeUpdate()
            }

            it("should be saved") {
                assertNotNull(em.createQuery("select h from Hero h where name = :name", Hero::class.java)
                    .setParameter("name", "Thor").resultList.firstOrNull())
            }

        }

    }

})