package support

import io.micronaut.context.ApplicationContext
import org.spekframework.spek2.dsl.*
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.lifecycle.MemoizedValue
import org.spekframework.spek2.meta.*
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import javax.persistence.EntityManager

fun Root.tx(body: RootTx.() -> Unit) {
    body(RootTx(this).also { it.init() })
}

class SuiteTx(
    private val root: RootTx,
    private val delegate: GroupBody,
    val applicationContext: ApplicationContext,
    val entityManager: EntityManager

) : LifecycleAware, TestContainer, GroupBody {

    @Synonym(SynonymType.GROUP)
    @Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
    fun describe(description: String, skip: Skip = Skip.No, body: SuiteTx.() -> Unit) {
        group(description, skip) {
            body(
                SuiteTx(
                    this@SuiteTx.root,
                    this,
                    this@SuiteTx.applicationContext,
                    this@SuiteTx.entityManager
                )
            )
        }
    }

    @Synonym(SynonymType.TEST)
    @Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
    fun it(description: String, skip: Skip = Skip.No, body: TestBody.() -> Unit) {
        test(description, skip, body)
    }

    override fun group(
        description: String,
        skip: Skip,
        defaultCachingMode: CachingMode,
        preserveExecutionOrder: Boolean,
        body: GroupBody.() -> Unit
    ) {
        delegate.group(description, skip, defaultCachingMode, preserveExecutionOrder, body)
    }

    override fun test(description: String, skip: Skip, body: TestBody.() -> Unit) {
        delegate.test(description, skip, RootTx.wrapWithTx(root._platformTransactionManager, body))
    }

    override fun <T> memoized(): MemoizedValue<T> {
        return delegate.memoized()
    }

    override val defaultCachingMode: CachingMode
        get() = delegate.defaultCachingMode

    override fun afterEachTest(callback: () -> Unit) {
        delegate.afterEachTest(RootTx.wrapWithTx(root._platformTransactionManager, callback))

    }

    override fun afterGroup(callback: () -> Unit) {
        delegate.afterGroup(RootTx.wrapWithTx(root._platformTransactionManager, callback))
    }

    override fun beforeEachTest(callback: () -> Unit) {
        delegate.beforeEachTest(RootTx.wrapWithTx(root._platformTransactionManager, callback))
    }

    override fun beforeGroup(callback: () -> Unit) {
        delegate.beforeGroup(RootTx.wrapWithTx(root._platformTransactionManager, callback))
    }

    override fun <T> memoized(mode: CachingMode, factory: () -> T): MemoizedValue<T> = delegate.memoized(mode, factory)

    override fun <T> memoized(mode: CachingMode, factory: () -> T, destructor: (T) -> Unit): MemoizedValue<T> =
        delegate.memoized(mode, factory, destructor)

    fun before(cb: () -> Unit) {
        beforeGroup(cb)
    }

    fun after(cb: () -> Unit) {
        afterGroup(cb)
    }

    fun beforeEach(cb: () -> Unit) {
        beforeEachTest(cb)
    }

    fun afterEach(cb: () -> Unit) {
        afterEachTest(cb)
    }
}

class RootTx(private val delegate: Root) : GroupBody, AutoCloseable {
    companion object {
        internal fun txScope(transactionManager: PlatformTransactionManager, body: (TransactionStatus) -> Unit): Any? {
            return TransactionTemplate(transactionManager).execute(body)
        }

        internal fun wrapWithTx(
            transactionManager: PlatformTransactionManager,
            body: TestBody.() -> Unit
        ): TestBody.() -> Unit {
            return {
                RootTx.txScope(transactionManager) {
                    this.apply(body)
                }
            }
        }

        internal fun wrapWithTx(transactionManager: PlatformTransactionManager, body: () -> Unit): () -> Unit {
            return {
                RootTx.txScope(transactionManager) {
                    body.invoke()
                }
            }
        }
    }

    private lateinit var _applicationContext: ApplicationContext
    internal lateinit var _platformTransactionManager: PlatformTransactionManager
    private lateinit var _entityManager: EntityManager


    fun init() {
        _applicationContext = ApplicationContext.run()
        _platformTransactionManager = _applicationContext.getBean(PlatformTransactionManager::class.java)
        _entityManager = _applicationContext.getBean(EntityManager::class.java)
    }

    @Synonym(SynonymType.GROUP)
    @Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
    fun describe(description: String, skip: Skip = Skip.No, body: SuiteTx.() -> Unit) {
        group(description, skip) {
            body(
                SuiteTx(
                    this@RootTx,
                    this,
                    this@RootTx._applicationContext,
                    this@RootTx._entityManager
                )
            )
        }
    }

    @Synonym(SynonymType.TEST)
    @Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
    fun it(description: String, skip: Skip = Skip.No, body: TestBody.() -> Unit) {
        test(description, skip, body)
    }


    override fun group(
        description: String,
        skip: Skip,
        defaultCachingMode: CachingMode,
        preserveExecutionOrder: Boolean,
        body: GroupBody.() -> Unit
    ) {
        delegate.group(description, skip, defaultCachingMode, preserveExecutionOrder, body)
    }

    override fun test(description: String, skip: Skip, body: TestBody.() -> Unit) {
        delegate.test(
            description = description,
            skip = skip,
            body = RootTx.wrapWithTx(_platformTransactionManager, body)
        )
    }

    override val defaultCachingMode: CachingMode
        get() = delegate.defaultCachingMode

    override fun afterEachTest(callback: () -> Unit) {
        delegate.afterEachTest(RootTx.wrapWithTx(_platformTransactionManager, callback))

    }

    override fun afterGroup(callback: () -> Unit) {
        delegate.afterGroup(RootTx.wrapWithTx(_platformTransactionManager, callback))
    }

    override fun beforeEachTest(callback: () -> Unit) {
        delegate.beforeEachTest(RootTx.wrapWithTx(_platformTransactionManager, callback))
    }

    override fun beforeGroup(callback: () -> Unit) {
        delegate.beforeGroup(RootTx.wrapWithTx(_platformTransactionManager, callback))
    }

    fun before(cb: () -> Unit) {
        beforeGroup(cb)
    }

    fun after(cb: () -> Unit) {
        afterGroup(cb)
    }

    fun beforeEach(cb: () -> Unit) {
        beforeEachTest(cb)
    }

    fun afterEach(cb: () -> Unit) {
        afterEachTest(cb)
    }


    override fun <T> memoized(mode: CachingMode, factory: () -> T): MemoizedValue<T> = memoized(mode, factory)

    override fun <T> memoized(mode: CachingMode, factory: () -> T, destructor: (T) -> Unit): MemoizedValue<T> =
        memoized(mode, factory, destructor)

    override fun <T> memoized(): MemoizedValue<T> = memoized()

    override fun close() {
        _applicationContext.close()
    }
}