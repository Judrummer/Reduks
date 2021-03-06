class Action
{
    class IncrA
    class IncrB
}
data class State(val a:Int=0,val b:Int=0)
val reducerA=Reducer<State>{ state,action-> when(action) {
    is Action.IncrA -> state.copy(a=state.a+1)
    else -> state
}}
val reducerB=Reducer<State>{ state,action-> when(action) {
    is Action.IncrB -> state.copy(b=state.b+1)
    else -> state
}}

val reducerAB=Reducer<State>{ state,action-> when(action) {
    is Action.IncrA -> state.copy(a=state.a*2)
    is Action.IncrB -> state.copy(b=state.b*2)
    else -> state
}}
val reducercombined= combineReducers(reducerA, reducerB, reducerAB)
