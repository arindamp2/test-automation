package core;

@SuppressWarnings("serial")
/**
 * Un-Checked Exception indicating a generic Lab Works Operation has gone wrong
 * Should be thrown by all function unit methods
 * @author Arindam
 *
 */
public class ScriptExecutionException extends RuntimeException {
	public ScriptExecutionException(String message){
		super(message);
	}

	public ScriptExecutionException(Exception e) {
		super(e);
	}
}
