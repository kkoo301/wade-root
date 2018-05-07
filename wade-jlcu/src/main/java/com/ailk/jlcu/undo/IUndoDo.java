package com.ailk.jlcu.undo;

import com.ailk.jlcu.mapunit.method.UndoMethod;

public interface IUndoDo {
	public abstract void undoDo(UndoMethod method) throws Exception;
}
