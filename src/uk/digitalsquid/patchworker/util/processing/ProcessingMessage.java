/*
 * This file is part of Patchworker
 * Patchworker - easily draw 9-patch images for Android development
 * Copyright (C) 2011 Will Shackleton
 *
 * Patchworker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Patchworker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Patchworker, in the file COPYING.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.patchworker.util.processing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * A message to be run on the processing thread
 * @author william
 *
 * @param <R>
 * 		The object to return to the UI thread
 */
public abstract class ProcessingMessage<R> {
	
	public ProcessingMessage() {}
	/**
	 * Constructs this message and tries to run it on the given thread.
	 * This should only be used when the message is created as an instance as soon as it is called.
	 * @param thread
	 */
	public ProcessingMessage(ProcessingThread thread) {
		try {
			thread.queueMessage(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public abstract R run();
	
	public abstract void done(R result);
	
	void doMessage() throws InterruptedException, InvocationTargetException {
		final R result = run();
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				done(result);
			}
		});
	}
}
