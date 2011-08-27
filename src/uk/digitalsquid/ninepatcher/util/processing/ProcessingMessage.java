package uk.digitalsquid.ninepatcher.util.processing;

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
