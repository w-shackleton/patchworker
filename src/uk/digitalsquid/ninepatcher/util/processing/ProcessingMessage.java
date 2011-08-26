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
