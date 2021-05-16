package figurefix.prac.lab.java8;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JsEngine {

	public static void main(String[] args) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		System.out.println(engine.getClass().getName());
		System.out.println("Result:" + engine.eval("function f() { return 1; }; f() + 1;"));

	}
}
