package com.brianegan.bansaDevTools

import com.brianegan.bansa.Reducer
import com.brianegan.bansa.StoreSubscriber
import com.brianegan.bansa.combineReducers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DevToolsStoreTest {
    data class TestState(val message: String = "initial state")
    data class TestAction(val type: String = "unknown")

    @Test
    fun `when an action is fired, the corresponding reducer should be called and update the state of the application`() {
        val reducer = Reducer<TestState> { state, action ->
            when (action) {
                is TestAction -> when (action.type) {
                    "to invoke" -> TestState("reduced")
                    else -> state
                }
                else -> state
            }
        }

        val store = DevToolsStore(TestState(), reducer)

        store.dispatch(TestAction(type = "to invoke"))

        assertThat(store.state.message).isEqualTo("reduced")
    }

    @Test
    fun `when two reducers are combined, and a series of actions are fired, the correct reducer should be called`() {
        val helloReducer1 = "helloReducer1"
        val helloReducer2 = "helloReducer2"

        val reducer1 = Reducer<TestState> { state, action ->
            when (action) {
                is TestAction -> when (action.type) {
                    helloReducer1 -> TestState("oh hai")
                    else -> state
                }
                else -> state
            }
        }

        val reducer2 = Reducer<TestState> { state, action ->
            when (action) {
                is TestAction -> when (action.type) {
                    helloReducer2 -> TestState("mark")
                    else -> state
                }
                else -> state
            }
        }

        val store = DevToolsStore(TestState(), combineReducers(reducer1, reducer2))

        store.dispatch(TestAction(type = helloReducer1))
        assertThat(store.state.message).isEqualTo("oh hai")
        store.dispatch(TestAction(type = helloReducer2))
        assertThat(store.state.message).isEqualTo("mark")
    }

    @Test
    fun `subscribers should be notified when the state changes`() {
        val store = DevToolsStore(TestState(), Reducer<TestState> { state, action -> TestState() })
        var subscriber1Called = false
        var subscriber2Called = false

        store.subscribe(StoreSubscriber { subscriber1Called = true })
        store.subscribe (StoreSubscriber { subscriber2Called = true })

        store.dispatch(TestAction())

        assertThat(subscriber1Called).isTrue()
        assertThat(subscriber2Called).isTrue()
    }

    @Test
    fun `the store should not notify unsubscribed objects`() {
        val store = DevToolsStore(TestState(), Reducer<TestState> { state, action -> TestState() })
        var subscriber1Called = false
        var subscriber2Called = false

        store.subscribe (StoreSubscriber { subscriber1Called = true })
        val subscription = store.subscribe(StoreSubscriber { subscriber2Called = true })
        subscription.unsubscribe()

        store.dispatch(TestAction())

        assertThat(subscriber1Called).isTrue()
        assertThat(subscriber2Called).isFalse()
    }

    @Test
    fun `store should pass the current state to subscribers`() {
        val reducer = Reducer<TestState> { state, action ->
            when (action) {
                is TestAction -> when (action.type) {
                    "to invoke" -> TestState("oh hai")
                    else -> state
                }
                else -> state
            }
        }

        var actual: TestState = TestState()
        val store = DevToolsStore(TestState(), reducer)

        store.subscribe(StoreSubscriber { actual = it })
        store.dispatch(TestAction(type = "to invoke"))

        assertThat(actual).isEqualTo(store.state)
    }

    @Test
    fun `store should work with both dev tools actions and application actions`() {
        val reducer = Reducer<TestState> { state, action ->
            when (action) {
                is TestAction -> when (action.type) {
                    "to invoke" -> TestState("oh hai")
                    else -> state
                }
                else -> state
            }
        }

        val store = DevToolsStore(TestState(), reducer)

        store.dispatch(TestAction(type = "to invoke"))
        assertThat(store.state.message).isEqualTo("oh hai")

        store.dispatch(DevToolsAction.createResetAction())
        assertThat(store.state.message).isEqualTo(TestState().message)
    }
}

