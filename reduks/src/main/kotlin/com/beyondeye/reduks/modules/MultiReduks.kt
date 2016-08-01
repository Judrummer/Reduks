package com.beyondeye.reduks.modules

import com.beyondeye.reduks.*
import com.beyondeye.reduks.middlewares.UnwrapActionMiddleware
import com.beyondeye.reduks.middlewares.applyMiddleware
import java.util.*

/**
 * same interface as regular reduks but allows to delegate actions to installed modules
 * Created by daely on 7/31/2016.
 */
data class MultiState2<S1:Any,S2:Any>(val s1:S1,val s2:S2)
data class MultiState3<S1:Any,S2:Any,S3:Any>(val s1:S1,val s2:S2,val s3:S3)


/**
 * base class for all MultiReduksN generic classes (MultiReduks2, MultiReduks3, ....)
 */
abstract class MultiReduks {
    internal var dispatchWrappedAction: (Any) -> Any = { action ->
        when(action) {
            is ActionWithContext -> {
                dispatchActionWithContext(action)
            }
            else -> throw IllegalArgumentException("Action missing context $action")
        }
    }
    internal abstract fun dispatchActionWithContext(a: ActionWithContext): Any
    companion object {
        fun <S1:Any,S2:Any>buildFromModules(m1:ReduksModuleDef<S1>,ctx1:ReduksContext,
                                            m2:ReduksModuleDef<S2>,ctx2:ReduksContext)=MultiReduks2(m1,ctx1,m2,ctx2)
    }
}

class MultiReduks2<S1:Any,S2:Any>(def1:ReduksModuleDef<S1>,ctx1:ReduksContext,
                                  def2:ReduksModuleDef<S2>,ctx2:ReduksContext) : MultiReduks(),Reduks<MultiState2<S1,S2>>{
    val r1=GenericReduks<S1>(def1,ctx1)
    val r2=GenericReduks<S2>(def2,ctx2)
    override fun dispatchActionWithContext(a: ActionWithContext): Any = when (a.context) {
            r1.context -> r1.dispatch(a.action)
            r2.context -> r2.dispatch(a.action)
            else -> throw IllegalArgumentException("no registered module with id ${a.context.moduleId}")
        }
    override val store= object:Store<MultiState2<S1, S2>> {
        override val state: MultiState2<S1, S2> get()= MultiState2(r1.store.state,r2.store.state)
        override var dispatch=dispatchWrappedAction
        override fun subscribe(storeSubscriber: StoreSubscriber<MultiState2<S1, S2>>): StoreSubscription {
            val s1=r1.subscribe(StoreSubscriber { storeSubscriber.onStateChange(state) })
            val s2=r2.subscribe(StoreSubscriber { storeSubscriber.onStateChange(state) })
            return MultiStoreSubscription(s1, s2)
        }
    }

    fun subscribe(storeSubscriber: StoreSubscriber<MultiState2<S1, S2>>): StoreSubscription =store.subscribe(storeSubscriber)
    /**
     * empty subscriber: if you want to add a subscriber on global state changes, call [subscribe] function above
     */
    override val storeSubscriber = StoreSubscriber<MultiState2<S1, S2>>{}

    /**
     * empty subscription: if you want to add a  susbscriber on global state changes, call [subscribe] function above
     */
    override val storeSubscription= StoreSubscription {}
    init {
//        r1.store.applyMiddleware(UnwrapActionMiddleware())
//        r2.store.applyMiddleware(UnwrapActionMiddleware())
        r1.store.dispatch(def1.startAction)
        r2.store.dispatch(def2.startAction)
    }


}



