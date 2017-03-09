package com.cinchfinancial.neofact.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.specs.BehaviorSpec
import jdk.nashorn.api.scripting.ScriptObjectMirror
import javax.script.Invocable
import javax.script.ScriptEngineManager

/**
 * Created by mark on 8/22/16.
 */
class ScriptSpec : BehaviorSpec() {
    init {

        Given("A javascript block") {
            val scriptEngine = ScriptEngineManager().getEngineByName("nashorn")
            val invoker = scriptEngine as Invocable
            val json = scriptEngine.eval("JSON") as ScriptObjectMirror
            println(json.callMember("parse", getJsonString()))
            val result = invoker.invokeMethod(json, "parse", getJsonString()) as ScriptObjectMirror
            scriptEngine.eval(getJSFunction1())
            invoker.invokeFunction("doit", result)
            scriptEngine.eval(getJSFunction2())
            val m = invoker.invokeFunction("another", result) as ScriptObjectMirror
            println(ObjectMapper().writeValueAsString(m))
        }
    }

    fun getJsonString() = """
{"name":"foo", "type":"bar", "description":"A description", "formula":true}
"""

    fun getJSFunction1() = """
function doit(r) { print(r.name+" "+r.formula); }
"""

    fun getJSFunction2() = """
function another(r) {
   var m = {}
   m.foo = {}
   m.foo.bar = r.formula
   return m
}
"""
}