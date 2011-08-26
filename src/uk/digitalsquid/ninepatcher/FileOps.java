package uk.digitalsquid.ninepatcher;

/**
 * File operations
 * @author william
 *
 */
public interface FileOps {
	public boolean open(String filename);
	public boolean export(String location, String imagename);
}
