package uk.digitalsquid.ninepatcher.util.processing;

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
	
	public void start() {
		thread.start();
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
