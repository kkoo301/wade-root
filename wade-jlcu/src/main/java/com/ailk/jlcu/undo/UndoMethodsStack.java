package com.ailk.jlcu.undo;

import java.util.Stack;

import com.ailk.jlcu.mapunit.method.UndoMethod;

public class UndoMethodsStack {
	
	private Stack<UndoMethod> stack;

	public UndoMethodsStack() {
		stack = new Stack<UndoMethod>();
	}

	public boolean empty() {
		return stack.empty();
	}

	public UndoMethod peek() {
		return stack.peek();
	}

	public UndoMethod pop() {
		return stack.pop();
	}

	public UndoMethod push(UndoMethod method) {
		return stack.push(method);
	}

	public void clear() {
		stack.clear();
	}
}
