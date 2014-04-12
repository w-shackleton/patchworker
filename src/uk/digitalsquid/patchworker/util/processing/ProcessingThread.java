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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread to process rendering and loading on. Batik isn't thread safe, so do everything in this thread.
 * @author william
 *
 */
public final class ProcessingThread {
	
	private static final int QUEUE_SIZE = 5;
	
	public ProcessingThread() {
		thread.setDaemon(true);
	}
	
	public boolean isFull() {
		return queue.size() >= QUEUE_SIZE;
	}
	
	/**
	 * Queues up an event, waiting if necessary
	 * @param msg
	 * @throws InterruptedException
	 */
	public void queueMessage(ProcessingMessage<?> msg) throws InterruptedException {
		if(!thread.isAlive()) thread.start();
		queue.put(msg);
	}
	
	private final BlockingQueue<ProcessingMessage<?>> queue = new LinkedBlockingQueue<ProcessingMessage<?>>(QUEUE_SIZE);
	
	private boolean stopping = false;
	
	private final Thread thread = new Thread("Processing Thread") {
		@Override
		public void run() {
			try {
			while(!stopping) {
				final ProcessingMessage<?> msg = queue.take();
				
				try {
					msg.doMessage();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			} catch(InterruptedException e) {
				return;
			}
		}
	};
	
	/**
	 * Stops the worker.
	 */
	public void stop() {
		stopping = true;
		thread.interrupt();
	}
}
